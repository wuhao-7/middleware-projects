package com.acme.middleware.rpc.service.discovery.jraft;

import com.alipay.sofa.jraft.Closure;
import com.alipay.sofa.jraft.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

/**
 * 服务操作回调
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class ServiceDiscoveryOperationClosure implements Closure {

    private final static Logger logger = LoggerFactory.getLogger(ServiceDiscoveryOperationClosure.class);

    private final ServiceDiscoveryOperation serviceDiscoveryOperation;

    private final BiConsumer<Status,Object> callback;

    private Object result;

    public ServiceDiscoveryOperationClosure(ServiceDiscoveryOperation serviceDiscoveryOperation, BiConsumer<Status, Object> callback) {
        this.serviceDiscoveryOperation = serviceDiscoveryOperation;
        this.callback = callback;
    }

    public ServiceDiscoveryOperation getServiceDiscoveryOperation() {
        return serviceDiscoveryOperation;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public void run(Status status) {
        logger.info("Run closure[status :{}] with operation: {}",status,serviceDiscoveryOperation);
        callback.accept(status,getResult());
    }
}
