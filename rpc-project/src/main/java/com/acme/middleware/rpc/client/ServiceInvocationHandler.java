package com.acme.middleware.rpc.client;

import com.acme.middleware.rpc.InvocationRequest;
import com.acme.middleware.rpc.loadbalancer.ServiceInstanceSelector;
import com.acme.middleware.rpc.service.ServiceInstance;
import com.acme.middleware.rpc.service.discovery.ServiceDiscovery;
import io.netty.channel.ChannelFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.acme.middleware.rpc.client.ExchangeFuture.createExchangeFuture;
import static com.acme.middleware.rpc.client.ExchangeFuture.removeExchangeFuture;

/**
 * 服务调用处理
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class ServiceInvocationHandler implements InvocationHandler {

    private String serviceName;

    private final RpcClient rpcClient;

    private final ServiceDiscovery serviceDiscovery;

    private final ServiceInstanceSelector selector;

    public ServiceInvocationHandler(String serviceName, RpcClient rpcClient) {
        this.serviceName = serviceName;
        this.rpcClient = rpcClient;
        this.serviceDiscovery = rpcClient.getServiceRegistry();
        this.selector = rpcClient.getSelector();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(isObjectDeclareMethod(method)){
            //本地处理
            return handleObjectMethod(proxy, method, args);
        }
        // 非object方法进行远程调用
        InvocationRequest request = createRequest(method, args);

        return execute(request,proxy);
    }

    private Object execute(InvocationRequest request,Object proxy){
        //在RPC 集群中选择一个实例
        ServiceInstance serviceInstance =selectServiceProviderInstance();
        //与目标 RPC 服务器建立连接
        ChannelFuture future = rpcClient.connect(serviceInstance);
        //发送请求消息 关联RequestId
        sendRequest(request,future);
        //创建请求对应的 Future 对象
        ExchangeFuture exchangeFuture = createExchangeFuture(request);

        try {
            // 阻塞 RPC 服务器响应，直到对方将response 对应requestId 设置到ExchangeFuture 所关联的Promise
            // 既Promise#setSuccess 或Promise#setFailure 被调用
            // 参考 InvocationResponseHandle
            return exchangeFuture.get();
        } catch (Exception e) {
            removeExchangeFuture(request.getRequestId());
        }
        throw new IllegalStateException("Invocation failed!");
    }

    private void sendRequest(InvocationRequest request, ChannelFuture future) {
        future.channel().writeAndFlush(request);
    }

    private ServiceInstance selectServiceProviderInstance() {
        List<ServiceInstance> serviceInstances = serviceDiscovery.getServiceInstance(serviceName);
        return selector.select(serviceInstances);
    }

    private InvocationRequest createRequest(Method method, Object[] args){
        InvocationRequest request = new InvocationRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setServiceName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        // todo
        request.setMetadata(new HashMap<>());
        return request;
    }

    private Object handleObjectMethod(Object proxy, Method method, Object[] args) {
        String methodName = method.getName();
        switch (methodName) {
            case "equals":
                // TODO
                break;
            case "hashCode":
                // TODO
                break;
            case "toString":
                // TODO
                break;
        }
        return null;
    }
    private boolean isObjectDeclareMethod(Method method){
        return Object.class == method.getDeclaringClass();
    }
}
