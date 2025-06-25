package com.acme.middleware.rpc.service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */
public class DefaultServiceInstance implements ServiceInstance{

    private String id;
    private String serviceName;
    private String host;
    private int port;
    private Map<String,String> metadata ;


    @Override
    public String getId() {
        if(id == null){
            return host+"-"+port;
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public Map<String, String> getMetadata() {
        if(metadata == null){
            metadata = new HashMap<>();
        }
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "DefaultServiceInstance{" +
                "id='" + id + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", metadata=" + metadata +
                '}';
    }
}
