package indi.mofan.apply.prime;


import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author mofan
 * @date 2025/3/11 15:34
 */
public final class MathUtils {
    private MathUtils() {
    }

    /**
     * 返回 n 个质数
     */
    public static Stream<Integer> primes(int n) {
        return Stream.iterate(2, i -> i + 1)
                .filter(MathUtils::isPrime)
                .limit(n);
    }

    private static boolean isPrime(int n) {
        int sqrt = (int) Math.sqrt(n);
        return IntStream.rangeClosed(2, sqrt)
                .noneMatch(i -> n % i == 0);
    }
}
