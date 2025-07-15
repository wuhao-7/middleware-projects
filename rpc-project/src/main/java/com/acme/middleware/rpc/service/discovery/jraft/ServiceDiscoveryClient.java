package com.acme.middleware.rpc.service.discovery.jraft;

import com.acme.middleware.rpc.service.discovery.jraft.ServiceDiscoveryGrpcHelper;
import com.alipay.sofa.jraft.RouteTable;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.option.CliOptions;
import com.alipay.sofa.jraft.rpc.CliClientService;
import com.alipay.sofa.jraft.rpc.RpcClient;
import com.alipay.sofa.jraft.rpc.impl.cli.CliClientServiceImpl;

import java.util.concurrent.TimeUnit;

/**
 * 服务发现客户端
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class ServiceDiscoveryClient {
    private String groupId = "service-discovery";
    private String registryAddress;

    private RpcClient rpcClient;
    private CliClientService cliClientService;

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public void init(){
        ServiceDiscoveryGrpcHelper.initGRpc();

        Configuration conf = new Configuration();
        if(!conf.parse(registryAddress)){
            throw new IllegalArgumentException("Fail to parse conf" + registryAddress);
        }

        RouteTable.getInstance().updateConfiguration(groupId,conf);

        CliClientServiceImpl cliClientService = new CliClientServiceImpl();
        cliClientService.init(new CliOptions());

        this.cliClientService = cliClientService;
        this.rpcClient = cliClientService.getRpcClient();
    }

    public <R> R invoke(Object request) throws Throwable {
        if(!RouteTable.getInstance().refreshLeader(cliClientService,groupId,1000).isOk()){
            throw new IllegalStateException();
        }

        PeerId leader = RouteTable.getInstance().selectLeader(groupId);
        return (R) rpcClient.invokeSync(leader.getEndpoint(),request, TimeUnit.SECONDS.toMillis(5));
    }
}
