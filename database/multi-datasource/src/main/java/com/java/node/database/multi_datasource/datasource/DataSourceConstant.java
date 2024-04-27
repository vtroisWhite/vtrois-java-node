package com.java.node.database.multi_datasource.datasource;

/**
 * @Description
 */
public interface DataSourceConstant {

    String DB1_config_prefix = "spring.datasource.db1";
    String DB1_basePackages = "com.java.node.database.multi_datasource.mapper.db1";
    String DB1_DataSource = "db1DataSource";
    String DB1_SqlSessionFactory = "db1SqlSessionFactory";
    String DB1_TransactionManager = "db1TransactionManager";
    String DB1_SqlSessionTemplate = "db1SqlSessionTemplate";
    String DB1_mapper_path = "classpath:mappers/db1/*.xml";
    String DB1_mybatis_config_path = "classpath:mybatis-config.xml";


    String DB2_config_prefix = "spring.datasource.db2";
    String DB2_basePackages = "com.java.node.database.multi_datasource.mapper.db2";
    String DB2_DataSource = "db2DataSource";
    String DB2_SqlSessionFactory = "db2SqlSessionFactory";
    String DB2_TransactionManager = "db2TransactionManager";
    String DB2_SqlSessionTemplate = "db2SqlSessionTemplate";
    String DB2_mapper_path = "classpath:mappers/db2/*.xml";
    String DB2_mybatis_config_path = "classpath:mybatis-config.xml";
}
