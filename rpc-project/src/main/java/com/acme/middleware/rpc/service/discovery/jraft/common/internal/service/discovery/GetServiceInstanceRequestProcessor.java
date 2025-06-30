package com.acme.middleware.rpc.service.discovery.jraft.common.internal.service.discovery;

import com.acme.middleware.rpc.service.ServiceInstance;
import com.acme.middleware.rpc.service.discovery.jraft.common.RequestContext;
import com.acme.middleware.rpc.service.discovery.jraft.common.RequestProcessor;
import com.acme.middleware.rpc.service.discovery.proto.ServiceDiscoveryOuter;
import com.alipay.sofa.jraft.Status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link ServiceDiscoveryOuter.GetServiceInstancesRequest} Processor
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class GetServiceInstanceRequestProcessor implements RequestProcessor<ServiceDiscoveryOuter.GetServiceInstancesRequest,ServiceDiscoveryOuter.GetServiceInstancesResponse> {
    private final ServiceDiscoveryRepository repository = ServiceDiscoveryRepository.getInstance();

    @Override
    public ServiceDiscoveryOuter.GetServiceInstancesResponse process(RequestContext<ServiceDiscoveryOuter.GetServiceInstancesRequest> requestContext, Status status) {
        ServiceDiscoveryOuter.GetServiceInstancesRequest request = requestContext.getData();
        String serviceName = request.getServiceName();
        Collection<ServiceInstance> serviceInstances = repository.getServiceInstance(serviceName);
        return response(serviceInstances);
    }

    private ServiceDiscoveryOuter.GetServiceInstancesResponse response(Collection<ServiceInstance> serviceInstances) {
        ServiceDiscoveryOuter.GetServiceInstancesResponse response =  ServiceDiscoveryOuter.GetServiceInstancesResponse.newBuilder()
                .addAllValue(adaptRegistrations(serviceInstances))
                .build();
        return response;
    }

    private List<ServiceDiscoveryOuter.Registration> adaptRegistrations(Collection<ServiceInstance> serviceInstances) {
        List<ServiceDiscoveryOuter.Registration> registrations = new ArrayList<>(serviceInstances.size());

        for (ServiceInstance serviceInstance:serviceInstances){
            registrations.add(adaptRegistration(serviceInstance));
        }
        return registrations;
    }

    private ServiceDiscoveryOuter.Registration adaptRegistration(ServiceInstance serviceInstance) {
        return ServiceDiscoveryOuter.Registration.newBuilder()
                .setId(serviceInstance.getId())
                .setServiceName(serviceInstance.getServiceName())
                .setHost(serviceInstance.getHost())
                .setPort(serviceInstance.getPort())
                .putAllMetadata(serviceInstance.getMetadata())
                .build();
    }
}
