package com.java.node.database.double_write.doubleWrite;

import cn.hutool.core.lang.Pair;
import com.java.node.database.double_write.datasource.DataSourceNameEnums;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.io.StringReader;
import java.util.*;

/**
 * @Description
 */
@Slf4j
@Intercepts(@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}))
public class MyInterceptor implements Interceptor {

    private static final List<String> doubleWriteTableList = Arrays.asList(
            "test_table_1"
    );

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        log.info("进入mybatis-拦截器-1");
        Object proceed = invocation.proceed();
        log.info("进入mybatis-拦截器-2");
        doubleWrite(invocation);
        return proceed;
    }

    /**
     * 数据双写
     *
     * @param invocation
     */
    private void doubleWrite(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        DataSourceNameEnums doubleWriteDb = DoubleWriteUtil.getDoubleWriteDb();
        if (doubleWriteDb == null) {
            return;
        }
        DataSource targetDataSource = ((AbstractRoutingDataSource) mappedStatement.getConfiguration().getEnvironment().getDataSource()).getResolvedDataSources().get(doubleWriteDb.getName());
        if (targetDataSource == null) {
            log.warn("双写目标数据源不存在,targetDataSourceNameEnums:{}", doubleWriteDb);
            return;
        }
        // 获取当前执行的 SQL 语句
        BoundSql boundSql = mappedStatement.getBoundSql(invocation.getArgs()[1]);
        String sql = boundSql.getSql();
        if (doubleWriteTableList.stream().noneMatch(sql::contains)) {
            //sql中不存在表关键字，不双写
            return;
        }
        String executeSql = DoubleWriteUtil.showSql(mappedStatement.getConfiguration(), boundSql);
        if (executeSql == null) {
            log.error("解析已执行sql失败:{}", sql);
            return;
        }
        log.info("当前执行的sql:{},写入的数据库:{}", sql, doubleWriteDb);
        Pair<Boolean, String> pair = isNeedDoubleWrite(executeSql);
        if (!pair.getKey()) {
            return;
        }
        String newSql = pair.getValue();
        log.info("需要双写的表,sql:{}", newSql);
        DoubleWriteUtil.submitTransactionTask(new DoubleWriteParam(doubleWriteDb, targetDataSource, Collections.singletonList(newSql)));
//        DoubleWriteUtil.useConnectionWrite(mappedStatement, newSql, doubleWriteDb, targetDataSource);
//        DoubleWriteUtil.useExecutorWrite(mappedStatement, newSql, doubleWriteDb, targetDataSource, invocation, boundSql);
    }

    private Pair<Boolean, String> isNeedDoubleWrite(String sql) {
        Statement statement;
        try {
            CCJSqlParserManager parserManager = new CCJSqlParserManager();
            statement = parserManager.parse(new StringReader(sql));
        } catch (Exception e) {
            log.error("sql解析失败,sql:{},e:{}", sql, e.getMessage());
            return Pair.of(false, null);
        }
        Optional<CCJSqlParserFunction.CCJSqlParserInterface> optional = CCJSqlParserFunction.list.stream().filter(x -> x.isMatch(statement)).findFirst();
        if (!optional.isPresent()) {
            log.error("sql解析类型失败:{}", sql);
            return Pair.of(false, null);
        }
        CCJSqlParserFunction.CCJSqlParserInterface ccjSqlParserInterface = optional.get();
        Statement parse = ccjSqlParserInterface.parse(statement);
        String tableName = ccjSqlParserInterface.getTableName(parse);
        if (tableName == null) {
            log.error("sql解析表名失败:{}", sql);
            return Pair.of(false, null);
        }
        tableName = tableName.replaceAll("`", "");
        if (doubleWriteTableList.stream().noneMatch(tableName::equalsIgnoreCase)) {
            return Pair.of(false, null);
        }
        String newSql = ccjSqlParserInterface.getNewSql(parse);
        return Pair.of(true, newSql);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
//        // 当目标类是要代理类型时，才包装目标类，否者直接返回目标本身,减少目标被代理的次数
//        return (target instanceof Executor) ? Plugin.wrap(target, this) : target;
    }

    @Override
    public void setProperties(Properties properties) {
    }

}