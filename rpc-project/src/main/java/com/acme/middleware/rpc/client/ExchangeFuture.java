package com.acme.middleware.rpc.client;

import com.acme.middleware.rpc.InvocationRequest;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.concurrent.*;

/**
 * {@link Future}
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */
public class ExchangeFuture implements Future {

    private final long created;

    private InvocationRequest request;

    private Promise promise;

    private static Map<String,ExchangeFuture> workingFutureMap = new ConcurrentHashMap<>();

    public static ExchangeFuture createExchangeFuture(InvocationRequest request){
        String requestId = request.getRequestId();
        return workingFutureMap.computeIfAbsent(requestId,id -> new ExchangeFuture(request));
    }

    public static ExchangeFuture removeExchangeFuture( String requestId){
        return workingFutureMap.remove(requestId);
    }

    public ExchangeFuture(InvocationRequest request) {
        this.created = System.currentTimeMillis();
        this.request = request;
        this.promise = new DefaultPromise(new DefaultEventLoop());
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return promise.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return promise.isCancelled();
    }

    @Override
    public boolean isDone() {
        return promise.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        return promise.get();
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return promise.get(timeout,unit);
    }

    public Promise getPromise() {
        return promise;
    }

    public void setPromise(Promise promise) {
        this.promise = promise;
    }
}
