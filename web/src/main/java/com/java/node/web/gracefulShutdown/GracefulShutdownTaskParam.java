package com.java.node.web.gracefulShutdown;

import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 线程池关闭时，要执行的任务
 */
@Slf4j
@Getter
@AllArgsConstructor
public class GracefulShutdownTaskParam<T> {
    private static final AtomicInteger atomicInteger = new AtomicInteger();
    /**
     * id 自增
     */
    private final int id;
    /**
     * 任务名称
     */
    private final String taskName;
    /**
     * 排序号码，越小越优先
     */
    private final int order;
    /**
     * 是否可以异步关闭
     */
    private final boolean asyncClose;
    /**
     * 关闭的对象
     */
    private final T obj;
    /**
     * 要执行的关闭方法
     */
    private final Consumer<T> taskFunction;

    public static <T> GracefulShutdownTaskParam<T> build(String taskName, T obj) {
        return build(taskName, null, null, obj, null);
    }

    public static <T> GracefulShutdownTaskParam<T> build(String taskName, Integer order, Boolean asyncClose, T obj, Consumer<T> taskFunction) {
        Assert.notNull(taskName, "关闭的任务名称不可为null");
        Assert.notNull(obj, "关闭的对象不可为null");
        if (ObjUtil.hasNull(obj, asyncClose, taskFunction)) {
            GracefulShutdownTaskParam defaultParam = getDefaultParam(taskName, obj);
            Assert.notNull(defaultParam, "缺少参数,且缺少默认的执行配置");
            if (order == null) {
                order = defaultParam.getOrder();
            }
            if (asyncClose == null) {
                asyncClose = defaultParam.isAsyncClose();
            }
            if (taskFunction == null) {
                taskFunction = defaultParam.getTaskFunction();
            }
        }
        int i = atomicInteger.incrementAndGet();
        return new GracefulShutdownTaskParam<>(i, taskName, order, asyncClose, obj, taskFunction);
    }

    private static GracefulShutdownTaskParam getDefaultParam(String taskName, Object obj) {
        if (obj instanceof ExecutorService) {
            //线程池
            return new GracefulShutdownTaskParam<>(0, null, 10_000, true, (ExecutorService) obj, (executor) -> {
                if (executor == null || executor.isShutdown()) {
                    log.info("优雅停机,线程池已经关闭:{},跳过", taskName);
                    return;
                }
                try {
                    executor.shutdown();
                    boolean flag = executor.awaitTermination(3, TimeUnit.SECONDS);
                    if (flag) {
                        log.info("优雅停机,线程池关闭完成:{}", taskName);
                    } else {
                        log.info("优雅停机,线程池关闭超时:{}", taskName);
                    }
                } catch (Exception e) {
                    log.info("优雅停机,线程池关闭异常:{},e:", taskName, e);
                }
            });
        }
        if (obj instanceof Thread) {
            //线程
            return new GracefulShutdownTaskParam<>(0, null, 10_000, true, (Thread) obj, (thread) -> {
                if (thread == null || !thread.isAlive()) {
                    log.info("优雅停机,线程已完成:{},跳过", taskName);
                    return;
                }
                try {
                    thread.join(1_000);
                    log.info("优雅停机,线程关闭完成:{}", taskName);
                } catch (InterruptedException e) {
                    log.error("优雅停机,线程等待超时未处理完成:{}", taskName);
                }
            });
        }
        return null;
    }
}
