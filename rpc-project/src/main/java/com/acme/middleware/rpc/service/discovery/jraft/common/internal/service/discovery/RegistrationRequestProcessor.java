package com.acme.middleware.rpc.service.discovery.jraft.common.internal.service.discovery;

import com.acme.middleware.rpc.service.DefaultServiceInstance;
import com.acme.middleware.rpc.service.ServiceInstance;
import com.acme.middleware.rpc.service.discovery.jraft.common.RequestContext;
import com.acme.middleware.rpc.service.discovery.jraft.common.RequestProcessor;
import com.acme.middleware.rpc.service.discovery.proto.ServiceDiscoveryOuter;
import com.alipay.sofa.jraft.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class RegistrationRequestProcessor implements RequestProcessor<ServiceDiscoveryOuter.Registration,ServiceDiscoveryOuter.Response> {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationRequestProcessor.class);

    private final ServiceDiscoveryRepository repository = ServiceDiscoveryRepository.getInstance();

    @Override
    public ServiceDiscoveryOuter.Response process(RequestContext<ServiceDiscoveryOuter.Registration> requestContext, Status status) {
        ServiceDiscoveryOuter.Registration registration = requestContext.getData();
        logger.info("Registration : {}",registration);
        ServiceInstance serviceInstance = adaptServiceInstance(registration);
        repository.registry(serviceInstance);
        return response(status);
    }

    private ServiceDiscoveryOuter.Response response(Status status) {
        ServiceDiscoveryOuter.Response response = ServiceDiscoveryOuter.Response.newBuilder()
                .setCode(status.getCode())
                .setMessage(status.getErrorMsg() == null ? "" : status.getErrorMsg())
                .build();
        return response;
    }

    private ServiceInstance adaptServiceInstance(ServiceDiscoveryOuter.Registration registration) {
        DefaultServiceInstance instance = new DefaultServiceInstance();
        instance.setId(registration.getId());
        instance.setServiceName(registration.getServiceName());
        instance.setHost(registration.getHost());
        instance.setPort(registration.getPort());
        instance.setMetadata(registration.getMetadataMap());
        return instance;
    }
}
