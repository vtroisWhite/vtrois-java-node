package com.java.node.database.double_write.doubleWrite;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;

@Slf4j
public class MyTransactionSynchronization implements TransactionSynchronization {

    @Override
    public void suspend() {
        // 事务挂起时的回调方法
        log.info("事务挂起时的回调方法");
        DoubleWriteTransactionUtil.suspendTransaction();
    }

    @Override
    public void resume() {
        // 事务恢复时的回调方法
        log.info("事务恢复时的回调方法");
        DoubleWriteTransactionUtil.resumeTransaction();
    }

    @Override
    public void beforeCommit(boolean readOnly) {
        // 事务提交前的回调方法
        log.info("事务提交前的回调方法,readOnly:" + readOnly);
    }

    @Override
    public void beforeCompletion() {
        // 事务完成前的回调方法
        log.info("事务完成前的回调方法");
    }

    @Override
    public void afterCommit() {
        // 事务提交后的回调方法
        log.info("事务提交后的回调方法");
    }

    @Override
    public void afterCompletion(int status) {
        // 事务完成后的回调方法
        log.info("事务完成后的回调方法,status:" + status);
        DoubleWriteTransactionUtil.closeTransaction(TransactionSynchronization.STATUS_COMMITTED == status);
    }
}
