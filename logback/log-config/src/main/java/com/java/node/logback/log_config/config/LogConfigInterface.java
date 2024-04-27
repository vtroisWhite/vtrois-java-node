package com.java.node.logback.log_config.config;

import org.springframework.context.ApplicationContext;

import java.util.Set;

/**
 * @Description
 */
public interface LogConfigInterface {

    String getName();

    void init(ApplicationContext context);

    void reload(ApplicationContext context, Set<String> keys);
}
