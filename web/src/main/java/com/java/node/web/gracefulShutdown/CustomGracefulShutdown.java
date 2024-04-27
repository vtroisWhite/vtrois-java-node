package com.java.node.web.gracefulShutdown;

import cn.hutool.core.thread.ThreadUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * @Description
 */
@Slf4j
@Component
public class CustomGracefulShutdown implements ApplicationListener<ContextClosedEvent> {

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        log.info("检测到进程退出,执行自定义优雅停机,检测方式:{}", "ApplicationListener<ContextClosedEvent>");
//        GracefulShutdownTaskUtil.doShutdown();
        ThreadUtil.sleep(500);
        log.info("执行结束");
    }

    @PreDestroy
    public void destroy() {
        log.info("检测到进程退出,执行自定义优雅停机,检测方式:{}", "@PreDestroy");
        ThreadUtil.sleep(500);
        log.info("执行结束");
    }

    @PostConstruct
    public void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("检测到进程退出,执行自定义优雅停机,检测方式:{}", "ShutdownHook");
            ThreadUtil.sleep(1500);
            log.info("执行结束 ShutdownHook");
        }));
    }
}
