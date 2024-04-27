package com.java.node.database.multi_datasource.datasource.db1;

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
@MapperScan(basePackages = {DataSourceConstant.DB1_basePackages}, sqlSessionFactoryRef = DataSourceConstant.DB1_SqlSessionFactory, sqlSessionTemplateRef = DataSourceConstant.DB1_SqlSessionTemplate)
public class DB1DataSourceConfig {

    @Primary
    @Bean(name = DataSourceConstant.DB1_DataSource)
    @ConfigurationProperties(prefix = DataSourceConstant.DB1_config_prefix)
    public DataSource dataSource() {
        return new DruidDataSource();
    }

    @Primary
    @Bean(name = DataSourceConstant.DB1_SqlSessionFactory)
    public SqlSessionFactory sqlSessionFactory(@Qualifier(DataSourceConstant.DB1_DataSource) DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(DataSourceConstant.DB1_mapper_path));
        bean.setConfigLocation(new DefaultResourceLoader().getResource(DataSourceConstant.DB1_mybatis_config_path));
        return bean.getObject();
    }

    @Primary
    @Bean(name = DataSourceConstant.DB1_TransactionManager)
    public DataSourceTransactionManager transactionManager(@Qualifier(DataSourceConstant.DB1_DataSource) DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Primary
    @Bean(name = DataSourceConstant.DB1_SqlSessionTemplate)
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier(DataSourceConstant.DB1_SqlSessionFactory) SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
