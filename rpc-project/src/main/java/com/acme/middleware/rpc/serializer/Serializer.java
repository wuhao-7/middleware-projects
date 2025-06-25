package com.acme.middleware.rpc.serializer;

import java.io.IOException;

import static com.acme.middleware.rpc.util.ServiceLoaders.loadDefault;

/**
 * 序列化/反序列化接口
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public interface Serializer {

    Serializer Default = loadDefault(Serializer.class);

    byte[] serialize(Object source) throws IOException;

    Object deserialize(byte[] bytes, Class<?> targetClass) throws IOException;

}
