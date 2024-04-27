package com.java.node.bug.scheduledAop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@EnableScheduling
public class AopTestController1 {
    /**
     * 示例1，使用@Autowired注入自己，定时任务调用不触发aop，异常情况
     */
    @Autowired
    private AopTestController1 thisService;

    @AopTest
    @GetMapping(value = "/aopTest1")
    @Scheduled(cron = "0/5 * * * * ?")
    public void test1() {
        log.info("-----------------------controller1 run------------");
    }
}
