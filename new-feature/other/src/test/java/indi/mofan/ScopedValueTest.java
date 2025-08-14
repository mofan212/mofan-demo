package indi.mofan;


import org.assertj.core.api.Assertions;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.StructuredTaskScope;

/**
 * @author mofan
 * @date 2025/8/13 16:32
 */
public class ScopedValueTest implements WithAssertions {

    @Test
    public void testThreadLocal() throws InterruptedException {
        ThreadLocal<String> threadLocal = new ThreadLocal<>();
        threadLocal.set("main-thread-value");

        Thread otherThread = new Thread(() -> {
            assertThat(threadLocal.get()).isNull();
            threadLocal.set("sub-thread-value");
        });

        otherThread.start();
        otherThread.join();

        assertThat(threadLocal.get()).isEqualTo("main-thread-value");
        threadLocal.remove();
    }

    private final static ScopedValue<String> CONTEXT = ScopedValue.newInstance();

    @Test
    public void testScopedValue() {

        // Bindings are per-thread
        ScopedValue.where(CONTEXT, "v").run(() -> {
            String v = CONTEXT.get();
            assertThat(v).isEqualTo("v");
        });

        ScopedValue.where(CONTEXT, "value")
                .run(() -> {
                    String v = CONTEXT.get();
                    assertThat(v).isEqualTo("value");

                    // Rebinding
                    ScopedValue.where(CONTEXT, "inner-value").run(() -> {
                        String inner = CONTEXT.get();
                        assertThat(inner).isEqualTo("inner-value");
                    });

                    v = CONTEXT.get();
                    assertThat(v).isEqualTo("value");

                    // Inheritance
                    try (var scope = new StructuredTaskScope<String>()) {
                        scope.fork(() -> {
                            String value = CONTEXT.get();
                            assertThat(value).isEqualTo("value");
                            return null;
                        });

                        scope.join();
                    } catch (Exception e) {
                        Assertions.fail(e);
                    }
                });

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> CONTEXT.orElseThrow(() -> new RuntimeException("no value")));
    }
}
