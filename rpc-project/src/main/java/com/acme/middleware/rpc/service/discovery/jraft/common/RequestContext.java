package com.acme.middleware.rpc.service.discovery.jraft.common;

import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.serialization.Serializer;
import com.alipay.remoting.serialization.SerializerManager;
import com.alipay.sofa.jraft.Node;

import java.io.Serializable;
import java.nio.ByteBuffer;

import static com.alipay.remoting.serialization.SerializerManager.Hessian2;

/**
 * Request Context
 * @param <T>
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class RequestContext<T extends Serializable> {
    private static final Serializer serializer = SerializerManager.getSerializer(Hessian2);

    private final T data;

    private final String dataType;

    private final Node node;

    private final JRaftStateMachine fsm;

    public RequestContext(T data, Node node, JRaftStateMachine fsm) {
        this.data = data;
        this.dataType = data.getClass().getName();
        this.fsm = fsm;
        this.node = node;
    }

    public T getData() {
        return data;
    }

    public String getDataType() {
        return dataType;
    }

    public Node getNode() {
        return node;
    }

    public JRaftStateMachine getFsm() {
        return fsm;
    }

    @Override
    public String toString() {
        return "RequestContext{" +
                "data=" + data +
                ", dataType='" + dataType + '\'' +
                ", node=" + node +
                ", fsm=" + fsm +
                '}';
    }

    public ByteBuffer serialize(){
        byte[] data = null;
        try {
            data = serializer.serialize(this.data);
        } catch (CodecException e) {
           throw new RuntimeException(e);
        }
        return ByteBuffer.wrap(data);
    }

    public static <V extends Serializable> RequestContext deserialize(ByteBuffer byteBuffer,Node node,JRaftStateMachine fsm){
        byte[] bytes = byteBuffer.array();
        RequestContext requestContext = null;
        try {
            V data = serializer.deserialize(bytes,RequestContext.class.getName());
            requestContext = new RequestContext(data,node,fsm);
        } catch (CodecException e) {
            throw new RuntimeException(e);
        }
        return requestContext;
    }
}
