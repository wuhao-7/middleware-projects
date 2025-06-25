package com.acme.middleware.rpc.codec;

import com.acme.middleware.rpc.InvocationRequest;
import com.acme.middleware.rpc.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link InvocationRequest} {@link MessageToByteEncoder}
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class MessageEncoder extends MessageToByteEncoder{

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
        Serializer serializer = Serializer.Default;
        byte[] data = serializer.serialize(message);
        out.writeInt(data.length);
        out.writeBytes(data);
        logger.info("Encode {} to bytes[length:{}]",message,data.length);
    }
}
