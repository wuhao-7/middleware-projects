package com.acme.middleware.rpc.service.discovery.jraft;

import com.acme.middleware.rpc.server.RpcServer;
import com.acme.middleware.rpc.service.discovery.proto.ServiceDiscoveryOuter;
import com.alipay.sofa.jraft.util.RpcFactoryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class ServiceDiscoveryGrpcHelper {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscoveryGrpcHelper.class);

    public static RpcServer rpcServer;

    public static void initGRpc(){
        if("com.alipay.sofa.jraft.rpc.impl.GrpcRaftRpcFactory".equals(RpcFactoryHelper.rpcFactory().getClass().getName())){
            RpcFactoryHelper.rpcFactory().registerProtobufSerializer(ServiceDiscoveryOuter.Registration.class.getName(),
                    ServiceDiscoveryOuter.Registration.getDefaultInstance());

            RpcFactoryHelper.rpcFactory().registerProtobufSerializer(ServiceDiscoveryOuter.Response.class.getName(),
                    ServiceDiscoveryOuter.Response.getDefaultInstance());

        }
    }

    public static void setRpcServer(RpcServer rpcServer) {
        ServiceDiscoveryGrpcHelper.rpcServer = rpcServer;
    }

    public static void blockUntilShutdown(){
        if (rpcServer == null){
            return;
        }
        if("com.alipay.sofa.jraft.rpc.impl.GrpcRaftRpcFactory".equals(RpcFactoryHelper.rpcFactory().getClass().getName())){
            try {
                Method getServer = rpcServer.getClass().getMethod("getServer");
                Object grpcServer = getServer.invoke(rpcServer);

                Method shutdown = grpcServer.getClass().getMethod("shutdown");
                Method awaitTerminationLimit = grpcServer.getClass().getMethod("awaitTermination", Long.class,
                        TimeUnit.class);

                Runtime.getRuntime().addShutdownHook(new Thread(){
                    @Override
                    public void run() {
                        try {
                            shutdown.invoke(grpcServer);
                            awaitTerminationLimit.invoke(grpcServer,30,TimeUnit.SECONDS);
                        } catch (Exception e) {
                            e.printStackTrace(System.err);
                        }
                    }
                });

                Method awaitTermination = grpcServer.getClass().getMethod("awaitTermination");
                awaitTermination.invoke(grpcServer);
            } catch (Exception e) {
                logger.error("Failed to block server", e);
            }


        }
    }

}
