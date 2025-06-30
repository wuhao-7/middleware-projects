package com.acme.middleware.rpc.service.discovery;

import com.acme.middleware.rpc.service.discovery.jraft.storage.InMemoryLogStorage;
import com.alipay.sofa.jraft.core.DefaultJRaftServiceFactory;
import com.alipay.sofa.jraft.option.RaftOptions;
import com.alipay.sofa.jraft.storage.LogStorage;

/**
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */
public class InMemoryJRaftServiceFactory extends DefaultJRaftServiceFactory {
    @Override
    public LogStorage createLogStorage(String uri, RaftOptions raftOptions) {
        return new InMemoryLogStorage();
    }
}
