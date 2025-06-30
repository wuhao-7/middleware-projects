package com.acme.middleware.rpc.service.discovery.jraft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务实列心跳线程
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class ServiceInstanceBeatThread extends Thread{
    private final ServiceDiscoveryStateMachine serviceDiscoveryStateMachine;
    private static final Logger logger = LoggerFactory.getLogger(ServiceInstanceBeatThread.class);

    public ServiceInstanceBeatThread(ServiceDiscoveryStateMachine serviceDiscoveryStateMachine) {
        super("service-instance-beat-check");
        this.serviceDiscoveryStateMachine = serviceDiscoveryStateMachine;
    }

    @Override
    public void run() {
        while (true){
            try{
                if (!serviceDiscoveryStateMachine.isLeader()){
                    serviceDiscoveryStateMachine.checkBeat();
                }
                Thread.sleep(5000);
            }catch (Exception ex){
                logger.error("error on check beat",ex);
                Thread.currentThread().isInterrupted();
            }
        }
    }
}
