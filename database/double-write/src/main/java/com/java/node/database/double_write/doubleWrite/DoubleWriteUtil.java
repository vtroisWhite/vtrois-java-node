package com.java.node.database.double_write.doubleWrite;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.java.node.database.double_write.datasource.DataSourceNameEnums;
import com.java.node.database.double_write.datasource.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.spring.transaction.SpringManagedTransaction;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description
 */
@Slf4j
public class DoubleWriteUtil {

    private static final Map<String, DataSourceNameEnums> doubleWriteTargetMap = new HashMap<String, DataSourceNameEnums>() {{
        put(null, DataSourceNameEnums.DB_2);
        put(DataSourceNameEnums.DB_1.getName(), DataSourceNameEnums.DB_2);
        put(DataSourceNameEnums.DB_2.getName(), DataSourceNameEnums.DB_1);
    }};

    private static final ExecutorService writeJob = Executors.newFixedThreadPool(1);

    public static void submitTransactionTask(DoubleWriteParam param) {
        if (DoubleWriteTransactionUtil.isInTransaction()) {
            DoubleWriteTransactionUtil.addSql(param.getExecutorSql());
        } else {
            submitTask(param);
        }
    }

    public static void submitTask(DoubleWriteParam param) {
        writeJob.execute(() -> {
            try {
                doWrite(param);
            } catch (Exception e) {
                log.warn("双写异常,targetDbName:{},executorSql:{},e:", param.getTargetDbName(), param.getExecutorSql(), e);
            }
        });
    }

    /**
     * 获取要双写到哪个数据库
     *
     * @return
     */
    public static DataSourceNameEnums getDoubleWriteDb() {
        String currentDb = DynamicDataSourceContextHolder.getDateSourceType();
        return doubleWriteTargetMap.get(currentDb);
    }

    public static void doWrite(DoubleWriteParam param) throws Exception {
        List<String> executorSql = param.getExecutorSql();
        if (CollUtil.isEmpty(executorSql)) {
            log.warn("没有要双写的sql");
            return;
        }
        DataSource targetDataSource = param.getTargetDataSource();
        Connection connection = targetDataSource.getConnection();
        connection.setAutoCommit(false);
        try {
            for (String sql : executorSql) {
                connection.createStatement().execute(sql);
            }
            connection.commit();
            log.info("双写完成,db:{},sql:{}", param.getTargetDbName(), executorSql);
        } catch (Throwable e) {
            log.error("写入数据库失败:{},e:", executorSql, e);
            connection.rollback();
        } finally {
            connection.close();
        }
    }

    private static String getParameterValue(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof String) {
            return "'" + obj.toString().replaceAll("'", "''") + "'";
        }
        if (obj instanceof Date) {
            return "'" + DateUtil.formatDateTime((Date) obj) + "'";
        }
        return obj.toString();
    }

    /**
     * 解析出执行的sql
     *
     * @param configuration
     * @param boundSql
     * @return
     */
    public static String showSql(Configuration configuration, BoundSql boundSql) {
        // 获取参数
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        // sql语句中多个空格都用一个空格代替
        StringBuilder sql = new StringBuilder(boundSql.getSql().replaceAll("[\\s]+", " "));
        try {
            List<Integer> placeholderIndex = getPlaceholderIndex(sql.toString());
            if (CollUtil.isNotEmpty(parameterMappings) && parameterObject != null) {
                // 获取类型处理器注册器，类型处理器的功能是进行java类型和数据库类型的转换
                TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                // 如果根据parameterObject.getClass(）可以找到对应的类型，则替换
                if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                    replacePlaceholder(sql, placeholderIndex.get(0), parameterObject);
                } else {
                    // MetaObject主要是封装了originalObject对象，提供了get和set的方法用于获取和设置originalObject的属性值,主要支持对JavaBean、Collection、Map三种类型对象的操作
                    MetaObject metaObject = configuration.newMetaObject(parameterObject);
                    for (int i = parameterMappings.size() - 1; i >= 0; i--) {
                        String propertyName = parameterMappings.get(i).getProperty();
                        if (metaObject.hasGetter(propertyName)) {
                            Object obj = metaObject.getValue(propertyName);
                            replacePlaceholder(sql, placeholderIndex.get(i), obj);
                        } else if (boundSql.hasAdditionalParameter(propertyName)) {
                            // 该分支是动态sql
                            Object obj = boundSql.getAdditionalParameter(propertyName);
                            replacePlaceholder(sql, placeholderIndex.get(i), obj);
                        } else {
                            log.error("获取sql填充值失败,propertyName:{},已处理的sql:{}", propertyName, sql);
                            return null;
                        }
                    }
                }
            }
            return sql.toString();
        } catch (Exception e) {
            log.error("sql解析异常,sql:{},parameterMappings:{},e:", sql, parameterMappings);
            return null;
        }
    }

    /**
     * 获取所有占位符下标
     */
    private static List<Integer> getPlaceholderIndex(String sql) {
        List<Integer> list = new ArrayList<>();
        int i = -1;
        while ((i = sql.indexOf("?", i + 1)) != -1) {
            list.add(i);
        }
        return list;
    }

    /**
     * 替换占位符
     *
     * @param index
     * @param data
     */
    private static void replacePlaceholder(StringBuilder sb, int index, Object data) {
        sb.replace(index, index + 1, getParameterValue(data));
//        sb.replace(index, index + 1, Matcher.quoteReplacement(getParameterValue(data)));
    }


    /**
     * 直接创建个Connection执行sql并自动提交事务，
     * <p>
     * 问题：无法和主库写入一起做到事务回滚
     */
    public static void useConnectionWrite(MappedStatement mappedStatement, String newSql, DataSourceNameEnums doubleWriteDb, DataSource dataSource) throws Throwable {
        Connection connection = dataSource.getConnection();
        connection.createStatement().executeUpdate(newSql);
    }

    /**
     * 创建个新数据库的Connection，替换掉 Executor 中已经存在的Connection，执行完成后再替换回来
     * <p>
     * 问题：再次使用Executor执行sql，担心对原因的sql执行会有影响，另外反正都要创建个新Connection，所以也没必要用这个方法
     */
    public static void useExecutorWrite(MappedStatement mappedStatement, String newSql, DataSourceNameEnums doubleWriteDb, DataSource dataSource, Invocation invocation, BoundSql boundSql) throws Throwable {
        Object arg = invocation.getArgs()[1];
        Executor executor = (Executor) invocation.getTarget();
        Transaction transaction = executor.getTransaction();
        // 获取类的私有字段
        Field privateField = SpringManagedTransaction.class.getDeclaredField("connection");
        // 设置字段的可访问性为true，即允许访问私有字段
        privateField.setAccessible(true);
        // 修改私有字段的值
        Object o = privateField.get(transaction);
        MappedStatement newMappedStatement = createMappedStatement(mappedStatement, boundSql, newSql);
        privateField.set(transaction, dataSource.getConnection());

        int res = executor.update(newMappedStatement, arg);
        log.info("双写结束:{}", res);
        privateField.set(transaction, o);
    }


    private static MappedStatement createMappedStatement(MappedStatement ms, BoundSql boundSql, String newSql) {
        MappedStatement.Builder builder = new MappedStatement.Builder(
                ms.getConfiguration(),
                ms.getId(),
                new StaticSqlSource(ms.getConfiguration(), newSql, boundSql.getParameterMappings()),
                ms.getSqlCommandType()
        );
        builder.resource(ms.getResource());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.timeout(ms.getTimeout());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        builder.keyProperty(buildProperties(ms.getKeyProperties()));
        builder.keyColumn(buildProperties(ms.getKeyColumns()));
        builder.databaseId(ms.getDatabaseId());
        builder.lang(ms.getLang());
        builder.fetchSize(ms.getFetchSize());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        builder.cache(ms.getCache());
        builder.resultOrdered(ms.isResultOrdered());
        builder.resultSets(buildProperties(ms.getResultSets()));
        builder.resultSetType(ms.getResultSetType());

        return builder.build();
    }

    private static String buildProperties(String[] strings) {
        if (strings == null || strings.length == 0) {
            return null;
        }
        StringBuilder keyProperties = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            keyProperties.append(strings[i]);
            if (strings.length - 1 > i) {
                keyProperties.append(",");
            }
        }
        return keyProperties.toString();
    }
}
