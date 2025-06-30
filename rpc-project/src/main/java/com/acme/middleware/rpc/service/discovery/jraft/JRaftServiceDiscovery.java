package com.acme.middleware.rpc.service.discovery.jraft;

import com.acme.middleware.rpc.service.ServiceInstance;
import com.acme.middleware.rpc.service.discovery.ServiceDiscovery;
import com.acme.middleware.rpc.service.discovery.proto.ServiceDiscoveryOuter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.acme.middleware.rpc.service.discovery.jraft.RegistrationRpcProcessor.adaptServiceInstance;

/**
 *
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class JRaftServiceDiscovery implements ServiceDiscovery {
    public static final String GROUP_ID_PROPERTY_NAME = "service.discovery.jraft.registry.group-id";

    public static final String DEFAULT_GROUP_ID_PROPERTY_VALUE = "service-discovery";

    public static final String REGISTRY_ADDRESS_PROPERTY_NAME  = "service.discovery.jraft.registry.address";

    private static final Logger log = LoggerFactory.getLogger(JRaftServiceDiscovery.class);

    private final ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();

    private ServiceDiscoveryClient client;

    @Override
    public void initialize(Map<String, Object> config) {
        String groupId = (String) config.getOrDefault(GROUP_ID_PROPERTY_NAME,DEFAULT_GROUP_ID_PROPERTY_VALUE);
        String registryAddress = (String) config.get(REGISTRY_ADDRESS_PROPERTY_NAME);

        ServiceDiscoveryClient client = new ServiceDiscoveryClient();
        client.setGroupId(groupId);
        client.setRegistryAddress(registryAddress);
        client.init();
        this.client = client;
    }

    @Override
    public void register(ServiceInstance serviceInstance) {
        register(serviceInstance,true);
    }

    private void register(ServiceInstance serviceInstance, boolean registered) {
        //调用RPC
        ServiceDiscoveryOuter.Registration registration = buildRegistration(serviceInstance,registered);
        try {
            client.invoke(registration);
        } catch (Throwable e) {
            log.error("Fail to register a server instance:" + serviceInstance,e);
        }
    }

    private ServiceDiscoveryOuter.Registration buildRegistration(ServiceInstance serviceInstance, boolean registered) {
        return ServiceDiscoveryOuter.Registration.newBuilder()
                .setId(serviceInstance.getId())
                .setServiceName(serviceInstance.getServiceName())
                .setHost(serviceInstance.getHost())
                .setPort(serviceInstance.getPort())
                .setReversed(!registered)
                .putAllMetadata(serviceInstance.getMetadata())
                .build();
    }

    @Override
    public void deregister(ServiceInstance serviceInstance) {
        register(serviceInstance,false);
    }

    @Override
    public List<ServiceInstance> getServiceInstances(String serviceName) {
        List<ServiceInstance> serviceInstances = Collections.emptyList();

        try {
            ServiceDiscoveryOuter.GetServiceInstancesResponse response = client.invoke(buildGetServiceInstanceRequest(serviceName));
            List<ServiceDiscoveryOuter.Registration> registrations = response.getValueList();
            serviceInstances = new ArrayList<>(registrations.size());
            for (ServiceDiscoveryOuter.Registration registration: registrations){
                serviceInstances.add(adaptServiceInstance(registration));
            }
        } catch (Throwable e) {
           log.error("Fail to get service instances by name : "+ serviceName,e);
        }
        return serviceInstances;
    }


    private ServiceDiscoveryOuter.GetServiceInstancesRequest buildGetServiceInstanceRequest(String serviceName) {
        return ServiceDiscoveryOuter.GetServiceInstancesRequest.newBuilder()
                .setServiceName(serviceName)
                .build();
    }

    @Override
    public void close() {

    }
}
