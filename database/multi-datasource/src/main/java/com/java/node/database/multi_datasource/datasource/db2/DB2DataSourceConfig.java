package com.java.node.database.multi_datasource.datasource.db2;

import com.alibaba.druid.pool.DruidDataSource;
import com.java.node.database.multi_datasource.datasource.DataSourceConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Slf4j
@Configuration
@MapperScan(basePackages = {DataSourceConstant.DB2_basePackages}, sqlSessionFactoryRef = DataSourceConstant.DB2_SqlSessionFactory, sqlSessionTemplateRef = DataSourceConstant.DB2_SqlSessionTemplate)
public class DB2DataSourceConfig {

    @Bean(name = DataSourceConstant.DB2_DataSource)
    @ConfigurationProperties(prefix = DataSourceConstant.DB2_config_prefix)
    public DataSource dataSource() {
        return new DruidDataSource();
    }

    @Bean(name = DataSourceConstant.DB2_SqlSessionFactory)
    public SqlSessionFactory sqlSessionFactory(@Qualifier(DataSourceConstant.DB2_DataSource) DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(DataSourceConstant.DB2_mapper_path));
        bean.setConfigLocation(new DefaultResourceLoader().getResource(DataSourceConstant.DB2_mybatis_config_path));
        return bean.getObject();
    }

    @Bean(name = DataSourceConstant.DB2_TransactionManager)
    public DataSourceTransactionManager transactionManager(@Qualifier(DataSourceConstant.DB2_DataSource) DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = DataSourceConstant.DB2_SqlSessionTemplate)
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier(DataSourceConstant.DB2_SqlSessionFactory) SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
