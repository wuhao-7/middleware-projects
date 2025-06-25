package com.acme.middleware.zookeeper.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryForever;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */
public class InterProcessMutexDemo {
    public static void main(String[] args) throws InterruptedException {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(new RetryForever(300))
                .build();
        client.start();

        InterProcessMutex lock = new InterProcessMutex(client,"/demo-locks");
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 3; i++) {
            executorService.execute(()->{
                try {
                    System.out.printf("线程[name : %s] 尝试获取锁\n", Thread.currentThread().getName());
                    lock.acquire();
                    System.out.printf("线程[name : %s] 已获取锁\n", Thread.currentThread().getName());
                    Thread.sleep(TimeUnit.SECONDS.toMillis(3));
                } catch (Exception e) {
                   //
                }finally {
                    try {
                        lock.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        if (!executorService.awaitTermination(4, TimeUnit.MINUTES)){
            executorService.shutdown();
        }


        client.close();
    }
}
