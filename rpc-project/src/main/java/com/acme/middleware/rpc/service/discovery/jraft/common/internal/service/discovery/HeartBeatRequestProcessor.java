package com.acme.middleware.rpc.service.discovery.jraft.common.internal.service.discovery;

import com.acme.middleware.rpc.service.discovery.jraft.common.RequestContext;
import com.acme.middleware.rpc.service.discovery.jraft.common.RequestProcessor;
import com.acme.middleware.rpc.service.discovery.proto.ServiceDiscoveryOuter;
import com.alipay.sofa.jraft.Status;

/**
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */
public class HeartBeatRequestProcessor implements RequestProcessor<ServiceDiscoveryOuter.HeartBeat, ServiceDiscoveryOuter.Response> {
    @Override
    public ServiceDiscoveryOuter.Response process(RequestContext<ServiceDiscoveryOuter.HeartBeat> requestContext, Status status) {

        return response(Status.OK());
    }


    private ServiceDiscoveryOuter.Response response(Status status) {
        ServiceDiscoveryOuter.Response response = ServiceDiscoveryOuter.Response.newBuilder()
                .setCode(status.getCode())
                .setMessage(status.getErrorMsg() == null ? "" : status.getErrorMsg())
                .build();
        return response;

    }
}
