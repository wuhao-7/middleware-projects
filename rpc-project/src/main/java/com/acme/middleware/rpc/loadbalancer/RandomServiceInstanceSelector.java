package com.acme.middleware.rpc.loadbalancer;

import com.acme.middleware.rpc.service.ServiceInstance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机 {@link  ServiceInstanceSelector}实现
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class RandomServiceInstanceSelector implements ServiceInstanceSelector{
    @Override
    public ServiceInstance select(List<ServiceInstance> serviceInstances) {
        int size = serviceInstances.size();
        int index  = ThreadLocalRandom.current().nextInt(size);
        return serviceInstances.get(index);
    }
}
