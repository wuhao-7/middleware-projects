package demo.juc;

import org.redisson.misc.Hash;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="kingyo7781@gmail.com">WuHao</a>
 * @since TODO
 */
public class ConcurrentHashMapDemo {

    public static void main(String[] args) throws InterruptedException {
        writeDemo();

    }

    private static void writeDemo() throws InterruptedException {
         Map<Integer, Integer> map = new HashMap<>();
//         ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();

        Thread writer1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                map.put(i, i);
            }
        });

        Thread writer2 = new Thread(() -> {
            for (int i = 1000; i < 2000; i++) {
                map.put(i, i);
            }
        });

        writer1.start();
        writer2.start();

        writer1.join();
        writer2.join();

        System.out.println("Final size: " + map.size());
    }
}
