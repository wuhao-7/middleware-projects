package com.acme.middleware.rpc.serializer;

import java.io.*;

/**
 * 默认{@link Serializer}
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class DefaultSerializer implements Serializer{

    @Override
    public byte[] serialize(Object source) throws IOException {
        byte[] bytes = null;
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
            ){
                // key -> byte[]
                objectOutputStream.writeObject(source);
                bytes = outputStream.toByteArray();

            }
        return bytes;
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> targetClass) throws IOException {
        if(bytes == null){
            return null;
        }
        Object value = null;
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)
        ){
            //byte[] -> key
            value = objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException();
        }
        return value;
    }
}

