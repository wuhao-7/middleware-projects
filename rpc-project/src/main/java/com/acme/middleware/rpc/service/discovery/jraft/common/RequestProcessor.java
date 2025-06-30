package com.acme.middleware.rpc.service.discovery.jraft.common;

import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rpc.RpcProcessor;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * RPC 请求处理器
 * @param <T> the type of request data
 * @param <R> the type of response data
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public interface RequestProcessor<T extends Serializable,R extends Serializable> {

    R process(RequestContext<T> requestContext, Status status);

    default String getRequestType(){
        String requestType = null;
        // TODO Get all generic interfaces
        Type[] genericInterfaces = this.getClass().getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                if (RequestProcessor.class.equals(parameterizedType.getRawType())) {
                    requestType = parameterizedType.getActualTypeArguments()[0].getTypeName();
                }
            }
        }
        return requestType;
    }

    default RpcProcessor<T> adapt(JRaftServer server){
        return new RequestProcessorRpcProcessAdapter(server,this);
    }
}
