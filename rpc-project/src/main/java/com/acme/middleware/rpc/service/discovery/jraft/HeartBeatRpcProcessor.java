package com.acme.middleware.rpc.service.discovery.jraft;

import com.acme.middleware.rpc.service.DefaultServiceInstance;
import com.acme.middleware.rpc.service.ServiceInstance;
import com.acme.middleware.rpc.service.discovery.proto.ServiceDiscoveryOuter;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.error.RaftError;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.acme.middleware.rpc.service.discovery.jraft.ServiceDiscoveryOperation.Kind.BEAT;

/**
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */
public class HeartBeatRpcProcessor implements RpcProcessor<ServiceDiscoveryOuter.HeartBeat> {

    private final static Logger logger = LoggerFactory.getLogger(HeartBeatRpcProcessor.class);

    private final ServiceDiscoveryServer server;

    public HeartBeatRpcProcessor(ServiceDiscoveryServer server) {
        this.server = server;
    }

    @Override
    public void handleRequest(RpcContext rpcCtx, ServiceDiscoveryOuter.HeartBeat heartBeat) {
        ServiceInstance serviceInstance = adaptServiceInstance(heartBeat);
        ServiceDiscoveryOperation.Kind kind = BEAT;
        ServiceDiscoveryOperation<ServiceInstance> operation = new ServiceDiscoveryOperation<>(kind,serviceInstance);

        ServiceDiscoveryOperationClosure closure  = new ServiceDiscoveryOperationClosure(operation,(status,result)->{
           if(!status.isOk()){
               logger.warn("Closure status is : {}",status);
               return;
           }
           // RPC 响应客户端
            rpcCtx.sendResponse(response(status));
           logger.info("heartBeat request has bean handled,status: {}",status);
        });

        if (!this.server.getFsm().isLeader()){
            closure.run(new Status(RaftError.EPERM,"Not leader"));
        }

        //心跳请求无须序列化到本地，对于Leader 节点来说直接执行即可
        this.server.getFsm().onBeat(serviceInstance);
        closure.run(Status.OK());
    }

    private ServiceDiscoveryOuter.Response response(Status status) {
        ServiceDiscoveryOuter.Response response = ServiceDiscoveryOuter.Response.newBuilder()
                .setCode(status.getCode())
                .setMessage(status.getErrorMsg() == null ? "" : status.getErrorMsg())
                .build();
        return response;
    }

    private ServiceInstance adaptServiceInstance(ServiceDiscoveryOuter.HeartBeat heartBeat) {
        DefaultServiceInstance serviceInstance = new DefaultServiceInstance();
        serviceInstance.setId(heartBeat.getId());
        serviceInstance.setServiceName(heartBeat.getServiceName());
        serviceInstance.setHost(heartBeat.getHost());
        serviceInstance.setPort(heartBeat.getPort());
        return serviceInstance;
    }

    @Override
    public String interest() {
        return ServiceDiscoveryOuter.HeartBeat.class.getName();
    }
}
