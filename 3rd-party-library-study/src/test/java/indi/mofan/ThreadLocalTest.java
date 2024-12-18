package indi.mofan;


import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author mofan
 * @date 2024/12/18 10:59
 * @link <a href="https://mp.weixin.qq.com/s/lmMXz6RC_Jv72xYCtOJkzg">这样实现异步线程间数据传递，太优雅了！</a>
 */
public class ThreadLocalTest implements WithAssertions {

    @Test
    @SneakyThrows
    public void testInheritableThreadLocal() {
        // 单一线程池
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        InheritableThreadLocal<String> username = new InheritableThreadLocal<>();
        for (int i = 0; i < 5; i++) {
            username.set("mofan-" + i);
            TimeUnit.SECONDS.sleep(1);
            CompletableFuture.runAsync(() -> assertThat(username.get()).isEqualTo("mofan-0"), executorService);
        }
    }

    @Test
    @SneakyThrows
    public void testTTL() {
        // 单一线程池
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        // 使用 TtlExecutors 对线程池进行包装
        executorService = TtlExecutors.getTtlExecutorService(executorService);
        // 换成 TTL
        TransmittableThreadLocal<String> username = new TransmittableThreadLocal<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            username.set("mofan-" + i);
            TimeUnit.SECONDS.sleep(1);
            CompletableFuture.runAsync(() -> {
                String pre = builder.toString();
                String cur = username.get();
                // 当前的信息与先前的不一样
                assertThat(cur).isNotEqualTo(pre);
                builder.delete(0, builder.length());
                builder.append(cur);
            }, executorService);
        }
    }
}
