package com.java.node.bug.scheduledAop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@EnableScheduling
public class AopTestController2 {

    /**
     * 示例2，没有使用@Autowired注入自己，定时任务调用正常触发aop，正常
     */
//    @Autowired
    private AopTestController1 thisService;

    @AopTest
    @GetMapping(value = "/aopTest2")
    @Scheduled(cron = "0/5 * * * * ?")
    public void test2() {
        log.info("-----------------------controller2 run------------");
    }
}
