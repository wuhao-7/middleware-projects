package com.acme.middleware.rpc.service.discovery;

import com.acme.middleware.rpc.service.ServiceInstance;

import java.util.List;
import java.util.Map;

import static com.acme.middleware.rpc.util.ServiceLoaders.loadDefault;

/**
 * 服务发现与注册
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public interface ServiceDiscovery {

    ServiceDiscovery DEFAULT = loadDefault(ServiceDiscovery.class);

    void initialize(Map<String, Object> config);

    void register(ServiceInstance serviceInstance);

    void deregister(ServiceInstance serviceInstance);

    List<ServiceInstance>  getServiceInstance(String serviceName);

    void close();
}
