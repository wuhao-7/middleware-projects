package com.acme.middleware.rpc.context;

import static com.acme.middleware.rpc.util.ServiceLoaders.loadDefault;

/**
 * 服务上下文
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public interface ServiceContext {
        ServiceContext DEFAULT =loadDefault(ServiceContext.class);

        boolean registerService(String serviceName,Object service);

        Object getService(String serviceName);
}
