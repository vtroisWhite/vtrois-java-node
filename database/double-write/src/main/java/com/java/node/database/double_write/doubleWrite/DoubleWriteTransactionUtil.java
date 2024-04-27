package com.java.node.database.double_write.doubleWrite;

import com.java.node.database.double_write.datasource.DataSourceNameEnums;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

/**
 * @Description
 */
@Slf4j
public class DoubleWriteTransactionUtil {

    private static final ThreadLocal<Boolean> transactionFlag = new ThreadLocal<>();

    private static final ThreadLocal<Stack<DoubleWriteParam>> doubleWriteParamThreadLocal = new ThreadLocal<>();

    /**
     * 是否开启了声明书事务
     *
     * @return
     */
    public static boolean isInTransaction() {
        return Boolean.TRUE.equals(transactionFlag.get());
    }

    /**
     * 开启声明事务
     */
    public static void openTransaction(DataSource dataSource) {
        log.info("声明式事务开启,使用批量sql提交");
        DataSourceNameEnums doubleWriteDb = DoubleWriteUtil.getDoubleWriteDb();
        if (doubleWriteDb == null) {
            return;
        }
        DataSource targetDataSource = ((AbstractRoutingDataSource) dataSource).getResolvedDataSources().get(doubleWriteDb.getName());
        if (targetDataSource == null) {
            log.warn("双写目标数据源不存在,targetDBName:{}", doubleWriteDb);
            return;
        }
        transactionFlag.set(true);
        addDoubleWriteParam(new DoubleWriteParam(doubleWriteDb, targetDataSource, new ArrayList<>()));
        TransactionSynchronizationManager.registerSynchronization(new MyTransactionSynchronization());
    }

    public static void addSql(Collection<String> sql) {
        DoubleWriteParam param = getDoubleWriteParam();
        if (param != null) {
            param.getExecutorSql().addAll(sql);
        }
    }

    /**
     * 关闭声明事务
     */
    public static void closeTransaction(boolean commit) {
        DoubleWriteParam param = removeDoubleWriteParam();
        if (!commit) {
            log.warn("声明式事务结束,事务未提交,不双写数据");
            return;
        }
        if (param == null || CollectionUtils.isEmpty(param.getExecutorSql())) {
            log.info("声明式事务结束,没有要执行的sql");
            return;
        }
        log.info("声明式事务结束,提交执行sql,长度:{}", param.getExecutorSql().size());
        DoubleWriteUtil.submitTask(param);
    }

    public static void suspendTransaction() {
        DoubleWriteParam doubleWriteParam = getDoubleWriteParam();
        if (doubleWriteParam != null) {
            doubleWriteParam.setTransactionSuspend(true);
        }
    }

    public static void resumeTransaction() {
        DoubleWriteParam doubleWriteParam = getDoubleWriteParam();
        if (doubleWriteParam != null) {
            doubleWriteParam.setTransactionSuspend(false);
        }
    }

    private static DoubleWriteParam getDoubleWriteParam() {
        Stack<DoubleWriteParam> doubleWriteParams = doubleWriteParamThreadLocal.get();
        if (CollectionUtils.isEmpty(doubleWriteParams)) {
            return null;
        }
        return doubleWriteParams.peek();
    }

    private static DoubleWriteParam removeDoubleWriteParam() {
        Stack<DoubleWriteParam> doubleWriteParams = doubleWriteParamThreadLocal.get();
        if (CollectionUtils.isEmpty(doubleWriteParams)) {
            return null;
        }
        DoubleWriteParam pop = doubleWriteParams.pop();
        if (CollectionUtils.isEmpty(doubleWriteParams)) {
            transactionFlag.remove();
        }
        return pop;
    }

    private static void addDoubleWriteParam(DoubleWriteParam param) {
        Stack<DoubleWriteParam> doubleWriteParams = doubleWriteParamThreadLocal.get();
        if (doubleWriteParams == null) {
            doubleWriteParams = new Stack<>();
            doubleWriteParamThreadLocal.set(doubleWriteParams);
        }
        if (doubleWriteParams.isEmpty()) {
            doubleWriteParams.push(param);
            return;
        }
        DoubleWriteParam preDoubleWriteParams = doubleWriteParams.peek();
        if (preDoubleWriteParams.isTransactionSuspend()) {
            log.info("当前事务挂起，创建个新的双写对象");
            doubleWriteParams.push(param);
            return;
        }
        log.error("声明式事务开启,但是当前线程存在未提及的sql");
        DoubleWriteUtil.submitTask(doubleWriteParams.pop());
        doubleWriteParams.push(param);
    }
}
