package indi.mofan.apply;


import indi.mofan.apply.prime.Eratosthenes;
import indi.mofan.apply.prime.MathUtils;
import indi.mofan.apply.prime.delay.LazyList;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

/**
 * @author mofan
 * @date 2025/3/10 21:55
 */
public class ApplyTest implements WithAssertions {

    static class Factorial {
        static int factorial(int n) {
            if (n == 0) return 1;
            return n * factorial(n - 1);
        }
    }

    @SuppressWarnings("unchecked")
    static class RecursionWithClosure {
        static Function<Integer, Integer>[] recursiveFunction = new Function[1];

        static {
            recursiveFunction[0] = x -> {
                if (x == 0) return 1;
                return x * recursiveFunction[0].apply(x - 1);
            };
        }

        static int factorial(int n) {
            return recursiveFunction[0].apply(n);
        }
    }

    static class Fn {
        Function<Integer, Integer> function;
    }

    static class RecursionFunc {
        static Fn fn = new Fn();

        static {
            fn.function = x -> {
                if (x == 0) return 1;
                return x * fn.function.apply(x - 1);
            };
        }

        static int factorial(int n) {
            return fn.function.apply(n);
        }
    }

    static class RecursionWithHigherOrderFunction {
        @FunctionalInterface
        interface SelfApplicable<SELF, T> {
            T apply(SelfApplicable<SELF, T> self, T t);
        }

        static final SelfApplicable<Integer, Integer> FACTORIAL = (self, n) ->
                n == 0 ? 1 : n * self.apply(self, n - 1);

        static int factorial(int n) {
            return FACTORIAL.apply(FACTORIAL, n);
        }
    }

    @Test
    public void testRecursion() {
        int value = Factorial.factorial(5);
        assertThat(value).isEqualTo(120);

        value = RecursionFunc.factorial(5);
        assertThat(value).isEqualTo(120);

        value = RecursionWithClosure.factorial(5);
        assertThat(value).isEqualTo(120);

        value = RecursionWithHigherOrderFunction.factorial(5);
        assertThat(value).isEqualTo(120);
    }

    @Test
    public void testLazyList() {
        LazyList<Integer> list = LazyList.from(2);
        Integer two = list.head();
        assertThat(two).isEqualTo(2);
        Integer three = list.tail().head();
        assertThat(three).isEqualTo(3);
        Integer four = list.tail().tail().head();
        assertThat(four).isEqualTo(4);
    }

    @Test
    public void testPrime() {
        List<Integer> primes = MathUtils.primes(3).toList();
        assertThat(primes).containsExactly(2, 3, 5);

        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> Eratosthenes.primesErr(Eratosthenes.numbers().limit(19)))
                .withMessageContaining("stream has already been operated upon or closed");

        LazyList<Integer> numbers = LazyList.from(2);
        Integer two = LazyList.primes(numbers).head();
        assertThat(two).isEqualTo(2);
        Integer three = LazyList.primes(numbers).tail().head();
        assertThat(three).isEqualTo(3);
        Integer five = LazyList.primes(numbers).tail().tail().head();
        assertThat(five).isEqualTo(5);
    }
}
