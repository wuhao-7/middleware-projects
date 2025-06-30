package com.acme.middleware.rpc.service.discovery.jraft;

import com.acme.middleware.rpc.service.discovery.jraft.storage.InMemoryLogStorage;
import com.alipay.sofa.jraft.JRaftServiceFactory;
import com.alipay.sofa.jraft.core.DefaultJRaftServiceFactory;
import com.alipay.sofa.jraft.option.RaftOptions;
import com.alipay.sofa.jraft.storage.LogStorage;

/**
 * 基于内存{@link JRaftServiceFactory}
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class InMemoryJRaftServiceFactory extends DefaultJRaftServiceFactory {
    @Override
    public LogStorage createLogStorage(String uri, RaftOptions raftOptions) {
        return new InMemoryLogStorage();
    }
}
