package com.acme.middleware.rpc.transport;

import com.acme.middleware.rpc.InvocationRequest;
import com.acme.middleware.rpc.InvocationResponse;
import com.acme.middleware.rpc.context.ServiceContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * {@link InvocationRequest} 处理器
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class InvocationRequestHandler extends SimpleChannelInboundHandler<InvocationRequest> {
    private final Logger logger = LoggerFactory.getLogger(InvocationRequestHandler.class);
    private final ServiceContext serviceContext;

    public InvocationRequestHandler(ServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InvocationRequest request) throws Exception {

            String serviceName = request.getServiceName();
            String methodName = request.getMethodName();
            Object[] parameters = request.getParameters();
            Class[] parameterTypes = request.getParameterTypes();

            Object service = serviceContext.getService(serviceName);
            Object entity = null;
            String errorMessage = null;

            try {
                entity =  MethodUtils.invokeMethod(service,methodName,parameters,parameterTypes);
            } catch (Exception e) {
                errorMessage = e.getMessage();
            }

        logger.info("Read {} and invoke the {}'s method[name:{}, param-types:{}, params:{}] : {}",
                request, serviceName, methodName, Arrays.asList(parameterTypes), Arrays.asList(parameters), entity);
        InvocationResponse response = new InvocationResponse();
        response.setRequestId(request.getRequestId());
        response.setEntity(entity);
        response.setErrorMessage(errorMessage);

        ctx.writeAndFlush(response);

        logger.info("Write and Flush {}", response);

    }
}
