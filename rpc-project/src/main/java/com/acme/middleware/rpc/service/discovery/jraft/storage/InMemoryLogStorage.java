package com.acme.middleware.rpc.service.discovery.jraft.storage;

import com.alipay.sofa.jraft.entity.LogEntry;
import com.alipay.sofa.jraft.option.LogStorageOptions;
import com.alipay.sofa.jraft.storage.LogStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 内存型 {@link LogStorage}
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since 1.0.0
 */
public class InMemoryLogStorage implements LogStorage {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock readLock = this.readWriteLock.readLock();

    private final Lock writeLock = this.readWriteLock.writeLock();

    private final ConcurrentSkipListMap<Long,LogEntry> storage = new ConcurrentSkipListMap<>();

    @Override
    public long getFirstLogIndex() {
        return storage.isEmpty() ? 1 : storage.firstKey();
    }

    @Override
    public long getLastLogIndex() {
        return storage.isEmpty() ? 0 : storage.lastKey();
    }

    @Override
    public LogEntry getEntry(long index) {
        return storage.get(index);
    }

    @Override
    @Deprecated
    public long getTerm(long index) {
        LogEntry entry = getEntry(index);
        return entry == null ? 0 : entry.getId().getTerm() ;
    }

    @Override
    public boolean appendEntry(LogEntry entry) {
        Long index  = entry.getId().getIndex();
        storage.put(index,entry);
        return true;
    }

    @Override
    public int appendEntries(List<LogEntry> entries) {
        int count = 0;
        for (LogEntry entry:entries){
            if(appendEntry(entry)) {
                count++;
            }
        }
        return count;
    }

    private boolean truncateEntries(Long fromIndex,
                                    boolean fromInclusive,
                                    Long toIndex,
                                    boolean toInclusive){
        Set<Map.Entry<Long,LogEntry>> entrySet = findEntries(fromIndex,fromInclusive,toIndex,toInclusive);
        if(!entrySet.isEmpty()){
            return false;
        }
        boolean removed = true;
        try {
            writeLock.lock();
            for (Map.Entry<Long,LogEntry> entry: entrySet){
                Long key = entry.getKey();
                LogEntry value = entry.getValue();
                removed &= storage.remove(key,value);
            }
        } finally {
            writeLock.unlock();
        }
        return removed;
    }

    private Set<Map.Entry<Long,LogEntry>> findEntries(Long fromIndex,
                                                      Boolean fromInclusive,
                                                      Long toIndex,
                                                      Boolean toInclusive){
        Set<Map.Entry<Long,LogEntry>> entries = null;
        try {
            readLock.lock();
            ConcurrentNavigableMap<Long,LogEntry> subMap = storage.subMap(fromIndex,fromInclusive,toIndex,toInclusive);
            entries = subMap.entrySet();
        } finally {
            readLock.unlock();
        }
        return entries;
    }

    @Override
    public boolean truncatePrefix(long firstIndexKept) {
        return truncateEntries(getFirstLogIndex(),true,firstIndexKept,false);
    }

    @Override
    public boolean truncateSuffix(long lastIndexKept) {
        return truncateEntries(lastIndexKept,false,getLastLogIndex(),true);
    }

    @Override
    public boolean reset(long nextLogIndex) {
        storage.clear();
        return false;
    }

    @Override
    public boolean init(LogStorageOptions opts) {
        return true;
    }

    @Override
    public void shutdown() {
        storage.clear();
    }
}
