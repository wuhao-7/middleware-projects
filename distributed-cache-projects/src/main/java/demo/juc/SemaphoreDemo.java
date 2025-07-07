package demo.juc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */
public class SemaphoreDemo {
    public static void main(String[] args) throws InterruptedException {
        Semaphore semaphore = new Semaphore(5);

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 5; i++) {
            executorService.submit(()->{
                try {
                    System.out.printf("[%s -线程执行 ]\n",Thread.currentThread().getName());
                    semaphore.acquire();
                    System.out.printf("[%s -线程 获取premise成功剩余 %d]\n",Thread.currentThread().getName(),semaphore.availablePermits());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        Thread.sleep(3 *1000);
        System.out.printf("[%s] 线程 尝试申请premise\n",Thread.currentThread().getName());
        executorService.submit(()->{
            try {
                Thread.sleep(2*1000);
                System.out.printf("[%s] -线程释放premise]\n",Thread.currentThread().getName());
                semaphore.release();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        semaphore.acquire();

        System.out.printf("[%s] 线程 申请成功\n",Thread.currentThread().getName());


        executorService.shutdown();


    }
}
