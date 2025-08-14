package indi.mofan.principle;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.function.BinaryOperator;

/**
 * @author mofan
 * @date 2024/7/20 16:37
 */
public class ClosurePrincipleTest implements WithAssertions {

    @Test
    public void test1() {
        int c = 10;
        BinaryOperator<Integer> lambda = (a, b) -> a + b + c;
        assertThat(lambda.apply(1, 2)).isEqualTo(13);
    }

    private static Integer lambda$test$(int c, Integer a, Integer b) {
        return a + b + c;
    }

    static final class MyClosure1 implements BinaryOperator<Integer> {
        private final int arg$1;

        private MyClosure1(int arg$1) {
            this.arg$1 = arg$1;
        }

        @Override
        public Integer apply(Integer a, Integer b) {
            return lambda$test$(arg$1, a, b);
        }
    }

    static int d = 100;

    @Test
    public void test2() {
        BinaryOperator<Integer> lambda = (a, b) -> a + b + d;
        assertThat(lambda.apply(1, 2)).isEqualTo(103);
        // 对于静态变量，并不要求其是 final 的
        d = 10;
        assertThat(lambda.apply(1, 2)).isEqualTo(13);
    }

    private static Integer lambda$test$$(Integer a, Integer b) {
        return a + b + ClosurePrincipleTest.d;
    }

    static final class MyClosure2 implements BinaryOperator<Integer> {
        public MyClosure2() {
        }

        @Override
        public Integer apply(Integer a, Integer b) {
            return lambda$test$$(a, b);
        }
    }

    int e = 1000;

    @Test
    public void test3() {
        BinaryOperator<Integer> lambda = (a, b) -> a + b + e;
        assertThat(lambda.apply(1, 2)).isEqualTo(1003);
        // 对于静态变量，也不要求其是 final 的
        e = 10;
        lambda = (a, b) -> a + b + e;
        assertThat(lambda.apply(1, 2)).isEqualTo(13);
    }

    private Integer lambda$test$$$(Integer a, Integer b) {
        return a + b + this.e;
    }

    static final class MyClosure3 implements BinaryOperator<Integer> {
        private final ClosurePrincipleTest arg$1;

        public MyClosure3(ClosurePrincipleTest arg$1) {
            this.arg$1 = arg$1;
        }

        @Override
        public Integer apply(Integer a, Integer b) {
            return this.arg$1.lambda$test$$$(a, b);
        }
    }
}
