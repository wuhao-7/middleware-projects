package com.acme.middleware.rpc.service.discovery.jraft;

import com.acme.middleware.rpc.service.ServiceInstance;
import com.alipay.sofa.jraft.Iterator;
import com.alipay.sofa.jraft.Node;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.core.StateMachineAdapter;
import com.alipay.sofa.jraft.entity.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static com.acme.middleware.rpc.service.discovery.jraft.ServiceDiscoveryOperation.Kind.DEREGISTRATION;

/**
 * 服务发现状态机
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class ServiceDiscoveryStateMachine extends StateMachineAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscoveryStateMachine.class);

    /**
     * 服务名称与服务实列列表（List） 映射
     */
    private final Map<String,Map<String , ServiceInstance>> serviceNameToInstanceStorage = new ConcurrentHashMap<>();

    /**
     * 记录服务实例存活时间
     */
    private final Map<String,Map<String, Instant>> serviceInstanceInstantMap = new ConcurrentHashMap<>();

    /**
     * 服务超过30s 没有心跳，则视为下线
     */
    private final int expired = 30;

    private final Object monitor = new Object();

    /**
     * leader term
     */
    private final AtomicLong leaderTerm = new AtomicLong(-1);

    private Node node;

    public void setNode(Node node) {
        this.node = node;
    }

    protected boolean isLeader(){
        return this.leaderTerm.get() > 0;
    }

    @Override
    public void onLeaderStart(long term) {
        this.leaderTerm.set(term);
        super.onLeaderStart(term);
    }

    @Override
    public void onLeaderStop(Status status) {
        this.leaderTerm.set(-1);
        super.onLeaderStop(status);
    }

    @Override
    public void onApply(final Iterator iter) {
        while(iter.hasNext()){
            ServiceDiscoveryOperation operation = null;
            ServiceDiscoveryOperationClosure closure = null;

            if(iter.done()!=null){
                // 从当前Leader 节点获取Closure
                closure = (ServiceDiscoveryOperationClosure) iter.done();
                operation = closure.getServiceDiscoveryOperation();
                logger.info("The closure with operation[{}] at the Leader node[{}]",operation,node);
            }else{
                // 在Follower 节点通过日志反序列化得到ServiceDiscoveryOperation
                final ByteBuffer data = iter.getData();
                operation = ServiceDiscoveryOperation.deserialize(data);
                logger.info("The closure with operation[{}] at the Follower node [{}]",operation,node);
            }

            if(operation!=null){
                ServiceDiscoveryOperation.Kind kind = operation.getKind();
                switch (kind){
                    case REGISTRATION:
                        //写入内存操作
                        register((ServiceInstance) operation.getData());
                        break;
                    case DEREGISTRATION:
                        deregister((ServiceInstance) operation.getData());
                        break;
                    case GET_SERVICE_INSTANCES:
                        getServiceInstances(closure,operation);
                        break;
                    case BEAT:
                       onBeat((ServiceInstance)operation.getData());
                }
                if(closure!=null){
                    closure.run(Status.OK());
                }
            }
            iter.next();
        }
    }
    //心跳请求
    public void onBeat(ServiceInstance serviceInstance) {
        String serviceName = serviceInstance.getServiceName();
        String id = serviceInstance.getId();

        synchronized (monitor){
            Map<String,ServiceInstance> serviceInstanceMap = serviceNameToInstanceStorage.computeIfAbsent(serviceName,
                    n->new LinkedHashMap<>());
            if(!serviceInstanceMap.containsKey(id)){
                //无效心跳请求
                logger.info("{} beat is invalid",id);
            }else{
                Instant now = Instant.now();
                Map<String,Instant> instantMap = this.serviceInstanceInstantMap.computeIfAbsent(serviceName,n-> new LinkedHashMap<>());
                instantMap.put(id,now);
            }
        }
        logger.info("{} has bean registered at the node[{}]",serviceInstance,node);
    }


    private void register(ServiceInstance serviceInstance){
        String serviceName  = serviceInstance.getServiceName();
        String id = serviceInstance.getId();

        synchronized (monitor){
            Map<String,ServiceInstance> serviceInstanceMap = serviceNameToInstanceStorage.computeIfAbsent(serviceName,n->new LinkedHashMap<>());
            serviceInstanceMap.put(id,serviceInstance);
            Instant now = Instant.now();
            Map<String,Instant> instantMap = this.serviceInstanceInstantMap.computeIfAbsent(serviceName,n->new LinkedHashMap<>());
            instantMap.put(id,now);
        }

        logger.info("{} has been registered at the node[{}]",serviceInstance,node);
    }

    private void deregister(ServiceInstance serviceInstance){
        String serviceName  = serviceInstance.getServiceName();
        String id = serviceInstance.getId();

        synchronized (monitor){
            Map<String,ServiceInstance> instanceMap = getServiceInstanceMap(serviceName);
            instanceMap.remove(id);
            Map<String,Instant>  instantMap = this.serviceInstanceInstantMap.get(serviceName);
            instantMap.remove(id);
        }
        logger.info("{} has bean deregistered at node[{}]",serviceInstance,node);
    }

    private void getServiceInstances(ServiceDiscoveryOperationClosure closure, ServiceDiscoveryOperation<String> operation){
        if(!isLeader()){
            return;
        }
        String serviceName = operation.getData();
        Map<String,ServiceInstance> serviceInstanceMap = getServiceInstanceMap(serviceName);
        closure.setResult(serviceInstanceMap.values());
    }
    private Map<String, ServiceInstance> getServiceInstanceMap(String serviceName) {
        return serviceNameToInstanceStorage.computeIfAbsent(serviceName,n->new LinkedHashMap<>());
    }

    public void checkBeat(){
        final Instant now = Instant.now();
        synchronized (monitor){
            for (Map.Entry<String,Map<String,ServiceInstance>> serviceInstanceMap : this.serviceNameToInstanceStorage.entrySet()){
                final String service  = serviceInstanceMap.getKey();
                final Map<String,Instant> instantMap = this.serviceInstanceInstantMap.get(service);
                if(instantMap == null || instantMap.isEmpty()){
                    // 当前服务所有实例都需要移除
                    serviceInstanceMap.getValue().clear();
                    continue;
                }
                List<String> needRemoveInstanceIds = new ArrayList<>();
                serviceInstanceMap.getValue().forEach((id,instance)->{
                    Instant instant = instantMap.get(id);
                    if(instant.plus(expired, ChronoUnit.SECONDS).isBefore(now)){
                        // 超过30s没有收到心跳
                        logger.info("The Current instance [{}] has not received a heartbeat request for more than 30 seconds",id);
                        needRemoveInstanceIds.add(id);
                    }
                });

                for(String id:needRemoveInstanceIds){
                    removeInstance(service,id);
                }

            }

        }
    }

    protected final void removeInstance(String service, String id) {
        Map<String,ServiceInstance> instanceMap = getServiceInstanceMap(service);
        ServiceInstance serviceInstance = instanceMap.get(id);
        if(serviceInstance != null){
            sendDeregistrationRpc(serviceInstance);
        }
    }

    /**
     * 向所有节点发送取消注册操作
     * @param serviceInstance
     */
    private void sendDeregistrationRpc(ServiceInstance serviceInstance) {
        if(!isLeader()){
            return;
        }
        ServiceDiscoveryOperation.Kind kind = DEREGISTRATION;

        ServiceDiscoveryOperation<ServiceInstance> operation = new ServiceDiscoveryOperation<>(kind,serviceInstance);

        ServiceDiscoveryOperationClosure closure = new ServiceDiscoveryOperationClosure(operation,(status,result)->{
           if(!status.isOk()){
               logger.warn("Closure status is : {}",status);
               return;
           }
           //rpc 响应到客户端
            logger.info("Deregistration request has been handled, status: {}",status);
        });
        Task task = new Task();
        task.setData(operation.serialize());
        task.setDone(closure);

        //提交任务
        this.node.apply(task);
    }
}
