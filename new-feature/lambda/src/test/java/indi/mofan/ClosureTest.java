package indi.mofan;

import lombok.AllArgsConstructor;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

/**
 * @author mofan
 * @date 2024/7/10 10:31
 */
public class ClosureTest implements WithAssertions {

    @FunctionalInterface
    private interface Op {
        Integer op(Integer param);
    }

    int NUMBER_INT = 2;
    static int STATIC_INT = 3;

    @Test
    public void testClosure() {
        int a = 1;
        // a = 2;
        // Lambda 表达式绑定了局部变量 a
        Op op = b -> a + b;
        assertThat(op.op(1)).isEqualTo(2);

        NUMBER_INT = 3;
        // Lambda 表达式绑定了成员变量
        op = b -> NUMBER_INT + b;
        assertThat(op.op(1)).isEqualTo(4);

        STATIC_INT = 4;
        // Lambda 表达式绑定了静态变量
        op = b -> STATIC_INT + b;
        assertThat(op.op(1)).isEqualTo(5);
    }

    @Test
    public void testHoldStatus() {
        int[] arr = {1};
        arr[0] = 2;
        Op op = b -> arr[0] + b;
        assertThat(op.op(1)).isEqualTo(3);
    }

    @AllArgsConstructor
    private static class Obj {
        private String text;
    }

    /**
     * FP：Functional Programming
     */
    @Test
    public void testBreakFP() {
        Obj obj = new Obj("hello");
        Function<String, String> func = i -> obj.text + " " + i;
        assertThat(func.apply("world")).isEqualTo("hello world");

        // 要求 obj 实例不变，但可以修改内部字段
        obj.text = "fxxk";
        assertThat(func.apply("world")).isEqualTo("fxxk world");
    }
}
