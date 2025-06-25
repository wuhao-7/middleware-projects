package com.acme.middleware.rpc.loadbalancer;

import com.acme.middleware.rpc.service.ServiceInstance;

import java.util.List;

import static com.acme.middleware.rpc.util.ServiceLoaders.loadDefault;

/**
 * {@link ServiceInstance}
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public interface ServiceInstanceSelector {

    ServiceInstanceSelector DEFAULT = loadDefault(ServiceInstanceSelector.class);

    ServiceInstance select(List<ServiceInstance> serviceInstances);
}
