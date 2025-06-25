package com.acme.middleware.rpc.service;

import java.io.Serializable;
import java.util.Map;

/**
 * 服务实例
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public interface ServiceInstance extends Serializable {

    String getId();

    String getServiceName();

    String getHost();

    int getPort();

    Map<String,String> getMetadata();
}
