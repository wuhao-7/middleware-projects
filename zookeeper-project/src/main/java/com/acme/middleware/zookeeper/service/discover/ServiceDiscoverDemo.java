package com.acme.middleware.zookeeper.service.discover;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * 服务发现与注册实例
 *
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */
public class ServiceDiscoverDemo {
    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(new RetryForever(300))
                .build();
        client.start();

        registryService(client);
        Thread.sleep(5000);

//        client.create().forPath("/temp","Hello,Word".getBytes(StandardCharsets.US_ASCII));

        client.close();
    }

    private static void registryService(CuratorFramework client) throws Exception {
        ServiceInstance instance = createInstance("demo-serviceInstance");

       ServiceDiscovery serviceDiscovery =  ServiceDiscoveryBuilder.builder(Map.class)
                .basePath("/demo-service")
                .client(client)
                .watchInstances(true)
                .build();

        serviceDiscovery.start();
        serviceDiscovery.registerService(instance);

    }

    private static ServiceInstance<Map<String,String>> createInstance(String serviceName) throws Exception {

        String id = UUID.randomUUID().toString().replaceAll("-","");
        Clock clock = Clock.systemUTC();
        ServiceInstance<Map<String,String>> serviceInstance = ServiceInstance.<Map<String,String>>builder()
                .id(id)
                .name(serviceName)
                .enabled(true)
                .address("127.0.0.1")
                .port(8080)
                .serviceType(ServiceType.PERMANENT)
                .payload(Collections.emptyMap())
                .registrationTimeUTC(clock.millis())
                .build();
        return serviceInstance;
    }
}
