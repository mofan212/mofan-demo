package indi.mofan;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

/**
 * 利用函数式接口模拟尾递归，避免栈溢出
 *
 * @author mofan
 * @date 2025/2/18 15:14
 */
public class TailRecursiveTest {

    interface TailRecursive<T> {

        TailRecursive<T> next();

        T result();

        static <T> T run(TailRecursive<T> initial) {
            TailRecursive<T> current = initial;
            while (true) {
                TailRecursive<T> next = current.next();
                if (next == null) return current.result();
                current = next;
            }
        }
    }

    static class Factorial {
        public static BigInteger calculate(int n) {
            return TailRecursive.run(new FactorialStep(BigInteger.ONE, n));
        }

        private record FactorialStep(BigInteger acc, int remaining) implements TailRecursive<BigInteger> {
            @Override
            public TailRecursive<BigInteger> next() {
                return (remaining == 0) ? null
                        : new FactorialStep(acc.multiply(BigInteger.valueOf(remaining)), remaining - 1);
            }

            @Override
            public BigInteger result() {
                return acc;
            }
        }
    }

    @Test
    public void testCalculate() {
        BigInteger res = Factorial.calculate(2);
        Assertions.assertEquals(BigInteger.TWO, res);

        res = Factorial.calculate(5);
        Assertions.assertEquals(BigInteger.valueOf(120), res);
    }
}
