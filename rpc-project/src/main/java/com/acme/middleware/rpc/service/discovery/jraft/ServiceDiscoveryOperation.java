package com.acme.middleware.rpc.service.discovery.jraft;

import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.serialization.Serializer;
import com.alipay.remoting.serialization.SerializerManager;

import java.io.Serializable;
import java.nio.ByteBuffer;

import static com.alipay.remoting.serialization.SerializerManager.Hessian2;

/**
 * 服务发现操作
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class ServiceDiscoveryOperation<V> implements Serializable {
    private static final Serializer serializer = SerializerManager.getSerializer(Hessian2);

    private final Kind kind;

    private final V data;

    public ServiceDiscoveryOperation(Kind kind, V data) {
        this.kind = kind;
        this.data = data;
    }

    public Kind getKind() {
        return kind;
    }

    public V getData() {
        return data;
    }

    public ByteBuffer serialize(){
        byte[] data = null;
        try {
            data = serializer.serialize(this);
        } catch (CodecException e) {
            throw new RuntimeException(e);
        }
        return ByteBuffer.wrap(data);
    }

    public static ServiceDiscoveryOperation deserialize(ByteBuffer data){
        byte[] bytes = data.array();
        ServiceDiscoveryOperation operation = null;
        try {
            operation = serializer.deserialize(bytes,ServiceDiscoveryOperation.class.getName());
        } catch (CodecException e) {
            throw new RuntimeException();
        }
        return operation;
    }


    @Override
    public String toString() {
        return "ServiceDiscoveryOperation{" +
                "kind=" + kind +
                ", data=" + data +
                '}';
    }

    public enum Kind {

        REGISTRATION,

        DEREGISTRATION,

        GET_SERVICE_INSTANCES,

        BEAT;
    }
}
