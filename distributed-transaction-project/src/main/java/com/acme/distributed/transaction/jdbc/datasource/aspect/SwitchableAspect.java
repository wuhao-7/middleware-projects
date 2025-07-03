package com.acme.distributed.transaction.jdbc.datasource.aspect;

import com.acme.distributed.transaction.jdbc.datasource.annotation.Switchable;
import com.acme.distributed.transaction.jdbc.datasource.util.DataSourceType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */

@Aspect
public class SwitchableAspect {

    @Pointcut("execution(* com.acme.middleware.distributed.transaction.service..*.*(..)) " +
            "& @target(com.acme.middleware.distributed.transaction.jdbc.datasource.annotation.Switchable)")
    private void switchable() {
    }

    @Around("switchable()")
    public Object switchDataSource(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Switchable switchable = method.getAnnotation(Switchable.class);
        Object result = null;
        try {
            if (switchable != null) {
                DataSourceType dataSourceType = switchable.dataSource();
                dataSourceType.switchDataSource();
            }
            result = pjp.proceed(pjp.getArgs());
        } finally {
            if (switchable != null) {
                DataSourceType.resetDataSource();
            }
        }
        return result;
    }
}
