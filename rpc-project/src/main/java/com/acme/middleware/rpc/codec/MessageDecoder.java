package com.acme.middleware.rpc.codec;

import com.acme.middleware.rpc.InvocationResponse;
import com.acme.middleware.rpc.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * {@link InvocationResponse} {@link ByteToMessageDecoder}
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class MessageDecoder extends ByteToMessageDecoder{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        int dataLength = in.readInt();
        if(in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Serializer serializer =Serializer.Default;
        Object object = serializer.deserialize(data,Object.class);
        out.add(object);
        logger.info("Serializer from bytes[length:{}] to be a {}",dataLength,object);
    }
}
