package com.acme.distributed.transaction.jdbc.datasource.util;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * {@link DataSource} 类型
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public enum DataSourceType {

    /**
     * 写 {@link DataSource}
     */
    WRITE("writeDataSource"),

    /**
     * 读 {@link DataSource}
     */
    READ("readDataSource");

    private final static ThreadLocal<String> dataSourceBeanNameHolder = ThreadLocal.withInitial(()->null);

    private  final String beanName;

    DataSourceType(String beanName) {
        this.beanName = beanName;
    }

    public static DataSourceType current() {
        String beanName = getDataSourceBeanName();
        DataSourceType current = DataSourceType.WRITE;
        if (beanName != null) {
            for (DataSourceType type : DataSourceType.values()) {
                if (Objects.equals(beanName, type.beanName)) {
                    current = type;
                    break;
                }
            }
        }
        return current;
    }


    public  void switchDataSource(){
        dataSourceBeanNameHolder.set(beanName);
    }

    public static String getDataSourceBeanName(){
        return dataSourceBeanNameHolder.get();
    }
    public static void resetDataSource(){
        dataSourceBeanNameHolder.remove();
    }

}
