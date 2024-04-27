package com.java.node.logback.log_config;

import com.java.node.logback.log_config.service.Service1;
import com.java.node.logback.log_config.service.Service2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class LogConfigApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(LogConfigApplication.class, args);
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(() -> {
            run.getBean(Service1.class).printLog();
            run.getBean(Service2.class).printLog();
        }, 5, 10, TimeUnit.SECONDS);
    }

}
