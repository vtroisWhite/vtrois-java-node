package com.java.node.database.double_write.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.java.node.database.double_write.doubleWrite.DoubleWriteTransactionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.DefaultTransactionStatus;

import javax.sql.DataSource;
import java.util.LinkedHashMap;

@Slf4j
@Configuration
@EnableTransactionManagement
@tk.mybatis.spring.annotation.MapperScan(basePackages = "com.java.node.database.double_write.mapper")
public class SessionFactoryConfig {

    private static final String mapperLocations = "classpath:mappers/*.xml";

    private static final String configLocation = "classpath:mybatis-config.xml";

    @Autowired
    private Environment environment;

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        LinkedHashMap<Object, Object> datasourceList = new LinkedHashMap<Object, Object>();
        //init datasource
        Binder binder = Binder.get(environment);
        String datasourceNameList = environment.getProperty("spring.datasource.name");
        for (String dsName : datasourceNameList.split(",")) {
            DruidDataSource druidDataSource = binder.bind("spring.datasource." + dsName, DruidDataSource.class).get();
            druidDataSource.setName(dsName);
            datasourceList.put(dsName, druidDataSource);
        }
        dynamicDataSource.setDefaultTargetDataSource(datasourceList.entrySet().stream().findFirst().get().getValue());
        dynamicDataSource.setTargetDataSources(datasourceList);
        dynamicDataSource.afterPropertiesSet();
        return dynamicDataSource;
    }


    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactorys(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        log.info("--------------------  sqlSessionFactory init ---------------------");
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        //设置mapper.xml文件所在位置
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources(mapperLocations);
        sessionFactoryBean.setMapperLocations(resources);
        //设置mybatis-config.xml配置文件位置
        sessionFactoryBean.setConfigLocation(new DefaultResourceLoader().getResource(configLocation));
        return sessionFactoryBean.getObject();
    }

    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public PlatformTransactionManager annotationDrivenTransactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource) {
            @Override
            protected void prepareSynchronization(DefaultTransactionStatus status, TransactionDefinition definition) {
                super.prepareSynchronization(status, definition);
                if (status.isNewSynchronization()) {
                    DoubleWriteTransactionUtil.openTransaction(dataSource);
                }
            }
        };
    }
}