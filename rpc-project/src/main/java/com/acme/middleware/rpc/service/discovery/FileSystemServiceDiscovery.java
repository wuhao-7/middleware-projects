package com.acme.middleware.rpc.service.discovery;

import com.acme.middleware.rpc.service.ServiceInstance;

import java.util.List;
import java.util.Map;

/**
 * 默认实现
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.o
 */
public class FileSystemServiceDiscovery implements ServiceDiscovery{
    @Override
    public void initialize(Map<String, Object> config) {

    }

    @Override
    public void register(ServiceInstance serviceInstance) {

    }

    @Override
    public void deregister(ServiceInstance serviceInstance) {

    }

    @Override
    public List<ServiceInstance> getServiceInstance(String serviceName) {
        return null;
    }

    @Override
    public void close() {

    }
}
