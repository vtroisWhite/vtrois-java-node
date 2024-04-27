package com.java.node.database.multi_datasource.datasource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component("druidDefaultProperties")
public class DruidDefaultProperties implements InitializingBean {
    @Override
    public void afterPropertiesSet() {
        System.setProperty("druid.maxWait", "60000");
        System.setProperty("druid.minIdle", "3");
        System.setProperty("druid.initialSize", "3");
        System.setProperty("druid.maxActive", "20");
        System.setProperty("druid.validationQuery", "SELECT 1 FROM DUAL");
        System.setProperty("druid.testOnBorrow", "true");
        System.setProperty("druid.testWhileIdle", "true");
        System.setProperty("druid.timeBetweenEvictionRunsMillis", "60000");
        System.setProperty("druid.minEvictableIdleTimeMillis", "30000");
        System.setProperty("druid.connectionProperties", "autoReconnect=true");
    }
}
