package com.acme.middleware.rpc.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存性{@link ServiceContext} 实现
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class InMemoryServiceContext implements ServiceContext{

    private final Map<String,Object> store = new ConcurrentHashMap<>();

    @Override
    public boolean registerService(String serviceName, Object service) {
        return store.putIfAbsent(serviceName, service) == null;
    }

    @Override
    public Object getService(String serviceName) {
        return store.get(serviceName);
    }
}
