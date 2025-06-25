package com.acme.middleware.rpc;

import java.io.Serializable;

/**
 * 响应上下文
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class InvocationResponse implements Serializable {

    private String requestId;

    private String errorMessage;

    private Object entity;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Object getEntity() {
        return entity;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    @Override
    public String toString() {
        return "InvocationResponse{" +
                "requestId='" + requestId + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", entity=" + entity +
                '}';
    }
}
