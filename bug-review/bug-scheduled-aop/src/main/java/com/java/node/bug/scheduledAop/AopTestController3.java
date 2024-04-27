package com.java.node.bug.scheduledAop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@EnableScheduling
public class AopTestController3 {

    /**
     * 示例1，虽然使用使用@Autowired注入自己，但使用了懒加载，定时任务调用正常触发aop
     */
    @Lazy
    @Autowired
    private AopTestController3 thisService;

    @AopTest
    @GetMapping(value = "/aopTest3")
    @Scheduled(cron = "0/5 * * * * ?")
    public void test3() {
        log.info("-----------------------controller3 run------------");
    }

}
