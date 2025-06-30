package com.acme.middleware.rpc.service.discovery.jraft.common;

import com.alipay.sofa.jraft.Closure;
import com.alipay.sofa.jraft.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */
public class RequestContextClosure implements Closure {

    private static final Logger logger = LoggerFactory.getLogger(RequestContextClosure.class);

    private final RequestContext requestContext;

    private final Closure delegate;

    private Object result;

    public RequestContextClosure(RequestContext requestContext, Closure closure) {
        this.requestContext = requestContext;
        this.delegate = closure;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public RequestContext getRequestContext() {
        return requestContext;
    }

    @Override
    public void run(Status status) {
        logger.info("Run closure[status: {}] with operation: {}", status,requestContext);
        delegate.run(status);
    }
}
