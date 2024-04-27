package com.java.node.web.gracefulShutdown;

import cn.hutool.core.thread.ThreadUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executors;

/**
 * @Description
 */
@Slf4j
@RestController
public class GracefulShutdownController {

    @Autowired
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        addTestTask();
        GracefulShutdownTaskUtil.doShutdown();
    }

    //    @PostConstruct
    public static void addTestTask() {
        GracefulShutdownTaskUtil.add(GracefulShutdownTaskParam.build("线程池-1", Executors.newFixedThreadPool(1)));
        GracefulShutdownTaskUtil.add(GracefulShutdownTaskParam.build("线程池-2", Executors.newFixedThreadPool(1)));
        GracefulShutdownTaskUtil.add(GracefulShutdownTaskParam.build("线程-2", new Thread()));
        GracefulShutdownTaskUtil.add(GracefulShutdownTaskParam.build("线程-1", new Thread()));
        GracefulShutdownTaskUtil.add(GracefulShutdownTaskParam.build("其他任务-1", 1, true, "", (x) -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }));
        GracefulShutdownTaskUtil.add(GracefulShutdownTaskParam.build("其他任务-2", 1, true, "", (x) -> {
        }));
        GracefulShutdownTaskUtil.add(GracefulShutdownTaskParam.build("其他任务-3", 1, false, "", (x) -> {
        }));
        GracefulShutdownTaskUtil.add(GracefulShutdownTaskParam.build("其他任务-4", 1, false, "", (x) -> {

        }));
    }

    @GetMapping("/gracefulShutdown/shutdown")
    public void test1() {
        new Thread(() -> {
            SpringApplication.exit(applicationContext);
        }).start();
        ThreadUtil.sleep(2000);
    }
}
