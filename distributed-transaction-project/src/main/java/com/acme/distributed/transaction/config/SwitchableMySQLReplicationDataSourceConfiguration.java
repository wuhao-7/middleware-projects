package com.acme.distributed.transaction.config;

import com.acme.distributed.transaction.jdbc.datasource.SwitchableMySQLReplicationDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.sql.DataSource;

/**
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */
public class SwitchableMySQLReplicationDataSourceConfiguration implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource) {
            return new SwitchableMySQLReplicationDataSource((DataSource) bean);
        }
        return bean;
    }
}
