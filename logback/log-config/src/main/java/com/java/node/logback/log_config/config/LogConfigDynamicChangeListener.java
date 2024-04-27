package com.java.node.logback.log_config.config;

import com.java.node.logback.log_config.config.impl.LogAppenderFilter;
import com.java.node.logback.log_config.config.impl.LogLevelChange;
import com.java.node.logback.log_config.config.impl.LogLevelFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 动态更新配置
 */
@Slf4j
@Component
public class LogConfigDynamicChangeListener implements ApplicationListener<ApplicationEvent> {

    private static final List<LogConfigInterface> funcList = Arrays.asList(
            new LogLevelChange(),
            new LogLevelFilter(),
            new LogAppenderFilter());
    @Autowired
    private ApplicationContext context;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        try {
            if (event instanceof ApplicationReadyEvent) {
                funcList.forEach(x -> CompletableFuture.runAsync(() -> x.init(context))
                        .whenComplete((res, ex) -> log.info("{}，初始化：{} ", x.getName(), ex == null ? "结束" : "异常", ex)));
            } else if (event instanceof EnvironmentChangeEvent changeEvent) {
                funcList.forEach(x -> CompletableFuture.runAsync(() -> x.reload(context, changeEvent.getKeys()))
                        .whenComplete((res, ex) -> log.info("{}，重载配置：{} ", x.getName(), ex == null ? "结束" : "异常", ex)));
            }
        } catch (Exception e) {
            log.error("动态刷新logback日志等级异常，e：", e);
        }
    }
}