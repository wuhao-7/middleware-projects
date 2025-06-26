package com.acme.middleware.rpc.demo;

import com.acme.middleware.rpc.server.RpcServer;

/**
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */
public class ServiceProvider {
    public static void main(String[] args) throws Exception {
        try(RpcServer serviceServer = new RpcServer("echoService",12345)){
            serviceServer.registerServer(EchoService.class.getName(),new DefaultEchoService());
            serviceServer.start();
        }
    }
}
