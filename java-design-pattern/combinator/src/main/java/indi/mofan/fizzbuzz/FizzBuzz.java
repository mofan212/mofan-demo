package indi.mofan.fizzbuzz;

import java.util.function.Function;

/**
 * @author mofan
 * @date 2024/7/23 14:07
 * @link <a href="https://gtrefs.github.io/code/functional-fizzbuzz/">Six Ways to functional FizzBuzz with Vavr</a>
 */
public class FizzBuzz {

    public static Function<Integer, String> fizzBuzz() {
        return FizzBuzzFunction.fizzbuzz()
                .orElse(FizzBuzzFunction.fizz())
                .orElse(FizzBuzzFunction.buzz())
                .orElse(FizzBuzzFunction.number());
    }

    private interface FizzBuzzFunction extends Function<Integer, String> {
        static FizzBuzzFunction word(String word) {
            return i -> word;
        }

        default FizzBuzzFunction ifDivisibleBy(int modulo) {
            return i -> i % modulo == 0 ? apply(i) : "";
        }

        static FizzBuzzFunction fizz() {
            return word("fizz").ifDivisibleBy(3);
        }

        static FizzBuzzFunction buzz() {
            return word("buzz").ifDivisibleBy(5);
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
