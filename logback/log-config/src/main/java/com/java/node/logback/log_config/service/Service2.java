package com.java.node.logback.log_config.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 */
@Slf4j
@Service
public class Service2 {

    public void printLog() {
        log.trace("trace-2");
        log.debug("debug-2");
        log.info("info-2");
        log.warn("warn-2");
        log.error("error-2");
    }

}
