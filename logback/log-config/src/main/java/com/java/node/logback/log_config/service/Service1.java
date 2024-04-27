package com.java.node.logback.log_config.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 */
@Slf4j
@Service
public class Service1 {

    public void printLog() {
        log.trace("trace-1");
        log.debug("debug-1");
        log.info("info-1");
        log.warn("warn-1");
        log.error("error-1");
    }

}
