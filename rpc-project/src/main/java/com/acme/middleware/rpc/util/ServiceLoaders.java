package com.acme.middleware.rpc.util;

import java.util.ServiceLoader;

/**
 * {@link java.util.ServiceLoader}
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public abstract class ServiceLoaders {
    public static <T> T loadDefault(Class<T> serviceClass){
            return ServiceLoader.load(serviceClass).iterator().next();
    }
}
