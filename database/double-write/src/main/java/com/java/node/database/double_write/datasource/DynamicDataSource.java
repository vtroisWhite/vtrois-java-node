package com.java.node.database.double_write.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {

    /**
     * 获得数据源
     */
    @Override
    protected Object determineCurrentLookupKey() {
        String dateSourceType = DynamicDataSourceContextHolder.getDateSourceType();
        logger.info("获取数据源:" + dateSourceType);
        return DynamicDataSourceContextHolder.getDateSourceType();
    }

    public DataSource getCurrentDataSource() {
        return super.determineTargetDataSource();
    }
}
