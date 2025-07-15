package com.acme.middleware.rpc.serializer.hessian;

import com.acme.middleware.rpc.serializer.Serializer;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class Hessian2Serializer implements Serializer {
    @Override
    public byte[] serialize(Object source) throws IOException {

        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            Hessian2Output output = new Hessian2Output(outputStream);
            output.writeObject(source);
            output.flush();
            return outputStream.toByteArray();
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> targetClass) throws IOException {
        if(bytes == null){
            return null;
        }
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)){
            Hessian2Input input =  new Hessian2Input(byteArrayInputStream);
            return  input.readObject();
        }
    }
}
