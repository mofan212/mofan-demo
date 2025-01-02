package indi.mofan.fizzbuzz;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author mofan
 * @date 2024/7/23 14:07
 * @link <a href="https://gtrefs.github.io/code/functional-fizzbuzz/">Six Ways to functional FizzBuzz with Vavr</a>
 */
public class FizzBuzz {

    private static final String FIZZ_WORD = "fizz";

    private static final String BUZZ_WORD = "buzz";

    private static final Map<Predicate<Integer>, String> PREDICATES = new LinkedHashMap<>(
            Map.of(
                    x -> x % 3 == 0,
                    FIZZ_WORD,
                    x -> x % 5 == 0,
                    BUZZ_WORD
            )
    );

    public static Function<Integer, String> fizzBuzz() {
        return FizzBuzzFunction.fizzbuzz()
                .orElse(FizzBuzzFunction.fizz())
                .orElse(FizzBuzzFunction.buzz())
                .orElse(FizzBuzzFunction.number());
    }

    /**
     * @link <a href="https://github.com/Ocean-Moist/FizzBuzz">Ocean-Moist/FizzBuzz</a>
     */
    public static String fizzBuzz(int number) {
        return Stream.of(number)
                .map(x -> PREDICATES.entrySet().stream()
                        .filter(entry -> entry.getKey().test(x))
                        .map(Map.Entry::getValue)
                        .reduce(String::concat)
                        .orElse(String.valueOf(x)))
                .collect(Collectors.joining(" "));
    }

    private interface FizzBuzzFunction extends Function<Integer, String> {
        static FizzBuzzFunction word(String word) {
            return i -> word;
        }

        default FizzBuzzFunction ifDivisibleBy(int modulo) {
            return i -> i % modulo == 0 ? apply(i) : "";
        }

        static FizzBuzzFunction fizz() {
            return word(FIZZ_WORD).ifDivisibleBy(3);
        }

        static FizzBuzzFunction buzz() {
            return word(BUZZ_WORD).ifDivisibleBy(5);
        }

        static FizzBuzzFunction fizzbuzz() {
            return fizz().and(buzz());
        }

        static FizzBuzzFunction number() {
            return String::valueOf;
        }

        default FizzBuzzFunction and(FizzBuzzFunction other) {
            return i -> {
                String first = this.apply(i);
                String second = other.apply(i);
                return (first.isEmpty() && second.isEmpty()) ? "" : first + second;
            };
        }

        default FizzBuzzFunction orElse(FizzBuzzFunction other) {
            return i -> {
                String first = this.apply(i);
                if (first.isEmpty()) {
                    return other.apply(i);
                }
                return first;
            };
        }
    }
}
