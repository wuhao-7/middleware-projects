package com.acme.middleware.rpc.service.discovery.jraft.common.internal.service.discovery;

import com.acme.middleware.rpc.service.ServiceInstance;
import com.acme.middleware.rpc.service.discovery.jraft.common.JRaftServer;
import com.acme.middleware.rpc.service.discovery.jraft.common.JRaftStateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ServiceDiscoveryRepository
 *
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class ServiceDiscoveryRepository {
    private Logger logger = LoggerFactory.getLogger(ServiceDiscoveryRepository.class);

    private static ServiceDiscoveryRepository instance = new ServiceDiscoveryRepository();

    private final Map<String, Map<String, ServiceInstance>> serviceNameToInstanceStorage = new ConcurrentHashMap<>();

    private JRaftStateMachine fsm;

    public void setFsm(JRaftStateMachine fsm) {
        this.fsm = fsm;
    }

    public void registry(ServiceInstance serviceInstance){
        String serviceName = serviceInstance.getServiceName();
        String id= serviceInstance.getId();

        synchronized (serviceNameToInstanceStorage){
            Map<String,ServiceInstance> serviceInstanceMap = serviceNameToInstanceStorage.computeIfAbsent(serviceName,
                    n->new LinkedHashMap<>());
            serviceInstanceMap.put(id,serviceInstance);
        }
        logger.info("{} has been registered", serviceInstance);
    }

    public void deregister(ServiceInstance serviceInstance){
        String serviceName = serviceInstance.getServiceName();
        String id = serviceInstance.getId();

        synchronized (serviceNameToInstanceStorage){
            Map<String, ServiceInstance> serviceInstanceMap = getServiceInstanceMap(serviceName);
           serviceInstanceMap.remove(id);
        }
        logger.info("{} has been deregistered", serviceInstance);
    }

    public Collection<ServiceInstance> getServiceInstance(String serviceName){
        Map<String, ServiceInstance> serviceInstanceMap = getServiceInstanceMap(serviceName);
        return serviceInstanceMap.values();
    }

    private boolean isLeader(){
        return fsm.isLeader();
    }

    private Map<String,ServiceInstance> getServiceInstanceMap(String serviceName){
        return serviceNameToInstanceStorage.computeIfAbsent(serviceName, n->new LinkedHashMap<>());
    }

    public static ServiceDiscoveryRepository getInstance(){
        return instance;
    }
}
