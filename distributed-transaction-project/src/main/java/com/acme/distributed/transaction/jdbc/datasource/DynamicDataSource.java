package com.acme.distributed.transaction.jdbc.datasource;

import com.acme.distributed.transaction.jdbc.datasource.util.DataSourceType;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceType.getDataSourceBeanName();
    }
}
