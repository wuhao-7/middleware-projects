package com.acme.middleware.rpc.demo;

import com.acme.middleware.rpc.client.RpcClient;

/**
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */
public class ServiceConsumer {

    public static void main(String[] args) throws Exception {
        try(RpcClient rpcClient = new RpcClient()){
            EchoService echoService = rpcClient.getService("echoService",EchoService.class);
            System.out.println(echoService.echo("hello,word"));
        }
    }
}
