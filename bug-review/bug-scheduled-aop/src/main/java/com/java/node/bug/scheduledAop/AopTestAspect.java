package com.java.node.bug.scheduledAop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AopTestAspect {

    @Around(value = "@annotation(aopTest)")
    public void aspect(ProceedingJoinPoint point, AopTest aopTest) throws Throwable {
        String logMethodName = point.getTarget().getClass().getSimpleName() + "." + point.getSignature().getName();
        log.info("--------------------aspect:{}-------------------------", logMethodName);
        point.proceed();
    }
}
