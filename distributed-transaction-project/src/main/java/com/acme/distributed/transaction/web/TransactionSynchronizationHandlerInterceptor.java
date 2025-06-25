package com.acme.distributed.transaction.web;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@link TransactionSynchronization} {@link HandlerInterceptor}
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */
public class TransactionSynchronizationHandlerInterceptor implements TransactionSynchronization ,HandlerInterceptor  {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        TransactionSynchronizationManager.initSynchronization();
        TransactionSynchronizationManager.registerSynchronization(this);
        return true;
    }

    @Override
    public void beforeCommit(boolean readOnly) {
        System.out.println(readOnly);
    }
}
