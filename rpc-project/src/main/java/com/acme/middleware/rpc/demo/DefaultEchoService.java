package com.acme.middleware.rpc.demo;

/**
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */
public class DefaultEchoService implements EchoService{
    @Override
    public String echo(String message) {
        return "[ECHO]: " + message;
    }
}
