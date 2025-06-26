package com.acme.middleware.rpc.client;

import com.acme.middleware.rpc.codec.MessageDecoder;
import com.acme.middleware.rpc.codec.MessageEncoder;
import com.acme.middleware.rpc.loadbalancer.ServiceInstanceSelector;
import com.acme.middleware.rpc.service.ServiceInstance;
import com.acme.middleware.rpc.service.discovery.ServiceDiscovery;
import com.acme.middleware.rpc.transport.InvocationRequestHandler;
import com.acme.middleware.rpc.transport.InvocationResponseHandle;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 客户端引导程序
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class RpcClient implements AutoCloseable {

    private final ServiceDiscovery serviceDiscovery;

    private final ServiceInstanceSelector selector;

    private final Bootstrap bootstrap;

    private final EventLoopGroup group;

    public RpcClient(ServiceDiscovery serviceDiscovery, ServiceInstanceSelector selector) {
        this.serviceDiscovery = serviceDiscovery;
        this.selector = selector;
        this.bootstrap = new Bootstrap();
        this.group = new NioEventLoopGroup();
        this.bootstrap.group(group)
                .option(ChannelOption.TCP_NODELAY,Boolean.TRUE)
                .option(ChannelOption.SO_KEEPALIVE,Boolean.TRUE)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("message-encoder",new MessageEncoder());
                        ch.pipeline().addLast("message-decoder",new MessageDecoder());
                        ch.pipeline().addLast("repose-handle",new InvocationResponseHandle());
                    }
                });

        serviceDiscovery.initialize((Map)System.getProperties());
    }

    public RpcClient(){
        this(ServiceDiscovery.DEFAULT,ServiceInstanceSelector.DEFAULT);
    }

    public <T> T getService(String serviceName, Class<T> serviceInterfaceClass) {
        ClassLoader classLoader = serviceInterfaceClass.getClassLoader();
        return (T) Proxy.newProxyInstance(classLoader, new Class[]{serviceInterfaceClass},
                new ServiceInvocationHandler(serviceName, this));
    }

    public ChannelFuture connect(ServiceInstance serviceInstance){
        String host = serviceInstance.getHost();
        int port = serviceInstance.getPort();
        ChannelFuture channelFuture = bootstrap.connect(host,port);
        return channelFuture.awaitUninterruptibly();
    }

    protected ServiceDiscovery getServiceRegistry() {
        return serviceDiscovery;
    }

    protected ServiceInstanceSelector getSelector() {
        return selector;
    }

    protected Bootstrap getBootstrap() {
        return bootstrap;
    }

    @Override
    public void close() throws Exception {
        group.shutdownGracefully();
    }
}
