package com.acme.distributed.transaction.config;

import com.acme.distributed.transaction.jdbc.datasource.DynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.BeanFactoryDataSourceLookup;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * {@link javax.sql.DataSource} 配置类
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@Profile("datasources")
public class DynamicDataSourceConfiguration {


    @Autowired
    private Environment environment;

    /**
     * 写 DataSource
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "datasources.write")
    public DataSource writeDataSource(){
        return new HikariDataSource();
    }

    /**
     * 读 DataSource
     *
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "datasources.read")
    public DataSource readDataSource(){
        return new HikariDataSource();
    }

    @Bean
    @Primary
    public DynamicDataSource dynamicDataSource(Map<String, DataSource> targetDataSources,
                                               @Qualifier("dataSourceLookup") DataSourceLookup dataSourceLookup) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources((Map) targetDataSources);
        dynamicDataSource.setDataSourceLookup(dataSourceLookup);
        // 设置 DataSource Bean 名称
        dynamicDataSource.setDefaultTargetDataSource("writeDataSource");
        return dynamicDataSource;
    }

    @Bean
    public BeanFactoryDataSourceLookup dataSourceLookup(){
        return new BeanFactoryDataSourceLookup();
    }

}
