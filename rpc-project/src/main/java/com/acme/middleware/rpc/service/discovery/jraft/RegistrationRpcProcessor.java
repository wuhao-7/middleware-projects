package com.acme.middleware.rpc.service.discovery.jraft;

import com.acme.middleware.rpc.service.DefaultServiceInstance;
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

import static com.acme.middleware.rpc.service.discovery.jraft.ServiceDiscoveryOperation.Kind.DEREGISTRATION;
import static com.acme.middleware.rpc.service.discovery.jraft.ServiceDiscoveryOperation.Kind.REGISTRATION;

/**
 * {@link ServiceDiscoveryOuter.Registration} 服务实例注册请求处理器
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class RegistrationRpcProcessor implements RpcProcessor<ServiceDiscoveryOuter.Registration> {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationRpcProcessor.class);

    private final ServiceDiscoveryServer server;

    public RegistrationRpcProcessor(ServiceDiscoveryServer server){
        this.server = server;
    }

    @Override
    public void handleRequest(RpcContext rpcCtx, ServiceDiscoveryOuter.Registration registration) {
        ServiceInstance serviceInstance = adaptServiceInstance(registration);
        ServiceDiscoveryOperation.Kind kind = registration.getReversed() ? DEREGISTRATION : REGISTRATION;

        ServiceDiscoveryOperation operation = new ServiceDiscoveryOperation(kind,serviceInstance);
        ServiceDiscoveryOperationClosure closure = new ServiceDiscoveryOperationClosure(operation,(status,result)->{
            if(!status.isOk()){
                logger.warn("Closure status is : {}",status);
                return;
            }
            // RPC 响应到客户端
            rpcCtx.sendResponse(response(status));
            logger.info("Registration request has bean handled,status:{}",status);
        });
        if(!isLeader()){
            handlerNotLeaderError(closure);
            return;
        }
        Task task = new Task();
        // 将注册的服务实例序列化为byte数组
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

    private ServiceDiscoveryStateMachine getFsm() {
        return this.server.getFsm();
    }

    private ServiceDiscoveryOuter.Response response(Status status) {
        ServiceDiscoveryOuter.Response response = ServiceDiscoveryOuter.Response.newBuilder()
                .setCode(status.getCode())
                .setMessage(status.getErrorMsg() == null ?"": status.getErrorMsg())
                .build();
        return response;
    }

    public static ServiceInstance adaptServiceInstance(ServiceDiscoveryOuter.Registration registration) {
        DefaultServiceInstance instance = new DefaultServiceInstance();
        instance.setId(registration.getId());
        instance.setServiceName(registration.getServiceName());
        instance.setHost(registration.getHost());
        instance.setPort(registration.getPort());
        instance.setMetadata(registration.getMetadataMap());
        return instance;
    }



    @Override
    public String interest() {
        return ServiceDiscoveryOuter.Registration.class.getName();
    }
}
