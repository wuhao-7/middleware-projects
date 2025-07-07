package demo.juc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */
public class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        // 使用Thread 实现
        // treadDemo(latch);

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 2; i++) {
            executorService.submit(()->{
                System.out.printf("[%s]- 线程开始执行!\n",Thread.currentThread().getName());
                latch.countDown();
            });
        }
        System.out.printf("[%s]- 线程开始等待!\n",Thread.currentThread().getName());
        latch.await();
        System.out.printf("[%s]- 线程结束等待!\n",Thread.currentThread().getName());
        executorService.shutdown();
        }

        private static void treadDemo(CountDownLatch latch) throws InterruptedException {
            Thread thread1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.printf("[%s]- 线程开始执行!\n",Thread.currentThread().getName());
                    latch.countDown();
                }
            });

            Thread thread2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.printf("[%s]- 线程开始执行!\n",Thread.currentThread().getName());
                    latch.countDown();
                }
            });

            thread1.start();
            thread2.start();
            System.out.printf("[%s]-线程进入等待\n",Thread.currentThread().getName());
            latch.await();
            System.out.printf("[%s]-线程结束等待\n",Thread.currentThread().getName());


        }


}
