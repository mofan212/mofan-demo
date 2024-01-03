package indi.mofan;

import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author mofan
 * @date 2024/1/3 22:05
 */
@SuppressWarnings("NonAtomicOperationOnVolatileField")
public class ReentrantLockTest implements WithAssertions {

    private volatile int num;

    private final Lock lock = new ReentrantLock();

    private int getNum() {
        return this.num;
    }

    private void addNum() {
        lock.lock();
        try {
            this.num++;
        } finally {
            lock.unlock();
        }
    }
    
    @Test
    @SneakyThrows
    public void test() {
        for (int i = 0; i < 100; i++) {
            new Thread(this::addNum).start();
        }

        // 睡一会，让所有线程的任务都执行完
        TimeUnit.MILLISECONDS.sleep(3000L);
        // 一般来说，都是 100
        assertThat(getNum()).isEqualTo(100);
    }
}
