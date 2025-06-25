package com.acme.middleware.rpc.loadbalancer;

import com.acme.middleware.rpc.service.ServiceInstance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询 {@link  ServiceInstanceSelector}实现
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */
public class RoundRobinServiceInstanceSelector implements ServiceInstanceSelector{

    private final AtomicInteger counter = new AtomicInteger();
    @Override
    public ServiceInstance select(List<ServiceInstance> serviceInstances) {
        int size = serviceInstances.size();
        int count = counter.getAndIncrement();
        int index = (count - 1) % size;
        return serviceInstances.get(index);
    }
}
