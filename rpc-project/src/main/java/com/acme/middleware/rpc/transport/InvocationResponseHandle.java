package com.acme.middleware.rpc.transport;

import com.acme.middleware.rpc.InvocationResponse;
import com.acme.middleware.rpc.client.ExchangeFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static com.acme.middleware.rpc.client.ExchangeFuture.removeExchangeFuture;

/**
 * {@link InvocationResponse}
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class InvocationResponseHandle extends SimpleChannelInboundHandler<InvocationResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, InvocationResponse response) throws Exception {
        //当RPC Server 成功响应时,requestId对象 promise(Future) 设置响应结果,并标记处理成功
        String requestId = response.getRequestId();
        ExchangeFuture exchangeFuture = removeExchangeFuture(requestId);
        if(exchangeFuture != null){
            Object result = response.getEntity();
            exchangeFuture.getPromise().setSuccess(result);
        }
    }
}
