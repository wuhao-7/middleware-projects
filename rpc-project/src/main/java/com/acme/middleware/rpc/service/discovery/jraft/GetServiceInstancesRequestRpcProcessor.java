package com.acme.middleware.rpc.service.discovery.jraft;

import com.acme.middleware.rpc.service.ServiceInstance;
import com.acme.middleware.rpc.service.discovery.proto.ServiceDiscoveryOuter;
import com.alipay.sofa.jraft.Closure;
import com.alipay.sofa.jraft.Node;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.entity.Task;
import com.alipay.sofa.jraft.error.RaftError;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.acme.middleware.rpc.service.discovery.jraft.ServiceDiscoveryOperation.Kind.GET_SERVICE_INSTANCES;

/**
 * {@link ServiceDiscoveryOuter.GetServiceInstancesRequest} 服务注册请求 处理器
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class GetServiceInstancesRequestRpcProcessor implements RpcProcessor<ServiceDiscoveryOuter.GetServiceInstancesRequest> {

    private static final Logger logger  = LoggerFactory.getLogger(GetServiceInstancesRequestRpcProcessor.class);

    private ServiceDiscoveryServer server;

    public GetServiceInstancesRequestRpcProcessor(ServiceDiscoveryServer server) {
        this.server = server;
    }

    @Override
    public void handleRequest(RpcContext rpcCtx, ServiceDiscoveryOuter.GetServiceInstancesRequest request) {
        final String serviceName = request.getServiceName();
        final ServiceDiscoveryOperation.Kind kind = GET_SERVICE_INSTANCES;
        ServiceDiscoveryOperation operation = new ServiceDiscoveryOperation(kind,serviceName);

        final ServiceDiscoveryOperationClosure closure = new ServiceDiscoveryOperationClosure(operation,(status,result)->{
            if(!status.isOk()){
                logger.warn("Closure status is : {} at the {}",status,server.getNode());
                return;
            }
            // RPC 响应客户端
            rpcCtx.sendResponse(response(result));
            logger.info("'{}' has been handled ,serviceName: '{}',result : {}, status : {}",
                    kind,serviceName,result,status);
        });
        if(!isLeader()){
            handlerNotLeaderError(closure);
            return;
        }

        Task task = new Task();
        //将注册的服务实例序列化为byte数组
        // 写入到本地日志, 将作为AppendEntries RPC 请求的来源 ->Followers
        task.setData(operation.serialize());
        //设置ServiceInstanceRegistrationClosure
        //触发Leader 节点上的状态机 -> ServiceInstanceStateMachine.onApply
        task.setDone(closure);
        //提交任务
        getNode().apply(task);

        logger.info("The task of '{}' has bean applied, serviceName: '{}' ", operation.getKind(),operation.getData());

    }

    private void handlerNotLeaderError(final Closure closure) {
        logger.error("No Leader node : {}",getNode());
        closure.run(new Status(RaftError.EPERM,"Not Leader"));
    }

    private Node getNode() {
        return this.server.getNode();
    }

    private boolean isLeader() {
        return getFsm().isLeader();
    }
    private ServiceDiscoveryStateMachine getFsm(){
        return this.server.getFsm();
    }

    private ServiceDiscoveryOuter.GetServiceInstancesResponse response(Object result) {
        Collection<ServiceInstance> serviceInstances = (Collection<ServiceInstance>) result;
        ServiceDiscoveryOuter.GetServiceInstancesResponse response = ServiceDiscoveryOuter.GetServiceInstancesResponse.newBuilder()
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

    @Override
    public String interest() {
        return ServiceDiscoveryOuter.GetServiceInstancesRequest.class.getName();
    }
}
