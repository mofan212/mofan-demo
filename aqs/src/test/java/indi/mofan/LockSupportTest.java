package indi.mofan;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * <p>三个线程打印 A、B、C，保证最终打印出的顺序总是 ABC。</p>
 * <p>
 * {@link LockSupport} 和使用它的线程都会关联一个许可，{@link LockSupport#park()} 方法
 * 表示消耗一个许可，调用该方法时，如果许可可用则直接返回，否则一直阻塞，直到许可可用。
 * {@link LockSupport#unpark(Thread)} 方法表示为某个线程添加许可，多次调用不会累加许可数量，
 * 许可数量最大值为 1。
 * </p>
 *
 * @author mofan
 * @date 2024/1/3 21:37
 */
public class LockSupportTest {

    private void printA(Thread thread) {
        System.out.println("A");
        LockSupport.unpark(thread);
    }

    private void printB(Thread thread) {
        LockSupport.park();
        System.out.println("B");
        LockSupport.unpark(thread);
    }

    private void printC() {
        LockSupport.park();
        System.out.println("C");
    }

    @Test
    @SneakyThrows
    public void test() {
        Thread c = new Thread(this::printC);
        Thread b = new Thread(() -> printB(c));
        Thread a = new Thread(() -> printA(b));

        a.start();
        b.start();
        c.start();

        // 睡一会
        TimeUnit.SECONDS.sleep(2L);
    }
}
