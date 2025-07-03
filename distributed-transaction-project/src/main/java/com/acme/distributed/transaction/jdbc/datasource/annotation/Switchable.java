package com.acme.distributed.transaction.jdbc.datasource.annotation;

import com.acme.distributed.transaction.jdbc.datasource.util.DataSourceType;

import java.lang.annotation.*;

/**
 * 可切换注解
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Switchable {
    DataSourceType dataSource() default DataSourceType.WRITE;
}
