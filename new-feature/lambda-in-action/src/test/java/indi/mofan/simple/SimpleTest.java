package indi.mofan.simple;

import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.assertj.core.util.CanIgnoreReturnValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static indi.mofan.simple.SimpleTest.Color.RED;

/**
 * @author mofan
 * @date 2025/3/2 13:35
 */
public class SimpleTest implements WithAssertions {

    // -------------------- 延迟执行 --------------------

    public void log(int level, String message) {
        if (level == 1) {
            System.out.println(message);
        }
    }

    public void log(int level, Supplier<String> log) {
        if (level == 1) {
            System.out.println(log.get());
        }
    }

    @Test
    public void testLog() {
        // 模拟多种日志信息
        String a = "A";
        String b = "B";
        String c = "C";

        log(2, a + b + c);
        log(1, a + b + c);

        log(2, () -> c + b + a);
        log(1, () -> c + b + a);
    }

    // -------------------- 高阶函数 --------------------

    public int add(int a, int b, IntUnaryOperator f) {
        return f.applyAsInt(a) + f.applyAsInt(b);
    }

    @Test
    public void testHigherOrderFunction() {
        int x = add(-5, 6, Math::abs);
        assertThat(x).isEqualTo(11);
    }

    // -------------------- 复合 Lambda 表达式 --------------------

    enum Color {
        RED, GREEN,
    }

    record Apple(Color color, int weight) {
    }

    @Test
    public void testPredicate() {
        Predicate<Apple> redApple = apple -> RED.equals(apple.color);

        // 不是红色的苹果
        Predicate<Apple> notRedApple = redApple.negate();
        Apple green130g = new Apple(Color.GREEN, 130);
        assertThat(notRedApple.test(green130g)).isTrue();

        // 红苹果且重量大于 150g
        Predicate<Apple> redAndHeavyApple = redApple.and(apple -> apple.weight > 150);
        Apple red160g = new Apple(RED, 160);
        assertThat(redAndHeavyApple.test(red160g)).isTrue();

        // 要么是 150g 以上的红苹果要么是绿苹果
        Predicate<Apple> redAndHeavyAppleOrGreen = redAndHeavyApple.or(apple -> Color.GREEN.equals(apple.color));
        assertThat(redAndHeavyAppleOrGreen.test(red160g)).isTrue();
        assertThat(redAndHeavyAppleOrGreen.test(green130g)).isTrue();
        assertThat(redAndHeavyAppleOrGreen.test(new Apple(RED, 149))).isFalse();
    }

    @Test
    public void testFunction() {
        Function<Integer, Integer> f = x -> x + 1;
        Function<Integer, Integer> g = x -> x * 2;

        // g(f(x))
        Function<Integer, Integer> x = f.andThen(g);
        assertThat(x.apply(1)).isEqualTo(4);

        // f(g(x))
        Function<Integer, Integer> y = f.compose(g);
        assertThat(y.apply(1)).isEqualTo(3);
    }

    // -------------------- 包装受检异常 --------------------

    public String processFile() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
            // 可能有更多的行为，比如一次读两行？
            return br.readLine();
        }
    }

    @CanIgnoreReturnValue
    public String processFileWithLambda1(Function<BufferedReader, String> function) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
            return function.apply(br);
        }
    }

    @FunctionalInterface
    public interface BufferedReaderProcessor {
        String process(BufferedReader br) throws IOException;
    }

    @CanIgnoreReturnValue
    public String processFileWithLambda2(BufferedReaderProcessor processor) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
            return processor.process(br);
        }
    }

    @FunctionalInterface
    public interface ThrowingFunction<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    public static <T, R> Function<T, R> wrap(ThrowingFunction<T, R, Exception> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Test
    @Disabled
    @SneakyThrows
    public void testProcessFile() {
        processFileWithLambda1(br -> {
            try {
                // 内置函数式接口不允许抛出受检异常
                return br.readLine() + br.readLine();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // 自定义函数式接口并声明受检异常
        processFileWithLambda2(br -> br.readLine() + br.readLine());

        // 包装受检异常
        processFileWithLambda1(wrap(br -> br.readLine() + br.readLine()));
    }

    public String readDataTxt(Supplier<String> supplier) {
        return supplier.get();
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T, E extends Exception> {
        T get() throws E;
    }

    public static <T, E extends Exception> Supplier<T> wrapSupplier(ThrowingSupplier<T, E> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable, R> R sneakyThrow(Throwable t) throws T {
        throw (T) t;
    }


    public static <T, E extends Exception> Supplier<T> sneakySupplier(ThrowingSupplier<T, E> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Exception e) {
                return sneakyThrow(e);
            }
        };
    }

    @Test
    public void testSneakyThrow() {
        try {
            String _ = readDataTxt(() -> {
                try {
                    return Files.readString(Path.of("data.txt"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertInstanceOf(RuntimeException.class, e);
        }

        try {
            String _ = readDataTxt(wrapSupplier(() -> Files.readString(Path.of("data.txt"))));
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertInstanceOf(RuntimeException.class, e);
        }

        try {
            String _ = readDataTxt(sneakySupplier(() -> Files.readString(Path.of("data.txt"))));
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertInstanceOf(IOException.class, e);
        }
    }

    // -------------------- 柯里化 --------------------
    // 见 indi.mofan.CurryingTest.testTernaryConsumer


    // -------------------- 类型推断 --------------------

    private void process(Consumer<String> consumer) {
    }

    private void process(Function<String, String> function) {
    }

    @Test
    public void testTypeInference() {
        // process(str -> System.out.println("Lambda In Action"));

        process((Consumer<String>) str -> System.out.println("Lambda In Action"));

        process((String str) -> System.out.println("Lambda In Action"));

        // 使用 x -> { foo(); } 与 void 兼容
        process(str -> {
            System.out.println("Lambda In Action");
        });

        // 使用 x -> ( foo() ) 与值兼容
        process(str -> (str.toLowerCase()));

        process(str -> "Lambda In Action");

        process(str -> {
        });
    }

    private void consumerIntFunction(Consumer<int[]> consumer) {
    }

    private void consumerIntFunction(Function<int[], ?> consumer) {
    }

    /**
     * <a href="https://stackoverflow.com/questions/29323520/reference-to-method-is-ambiguous-when-using-lambdas-and-generics/">Reference to method is ambiguous when using lambdas and generics</a>
     */
    @Test
    public void testMethodReferenceTypeInference() {

        consumerIntFunction(data -> {
            // 与 void 兼容的块，可以通过表达式识别，无需解析实际类型
            Arrays.sort(data);
        });

        /*
         * 如果是方法引用或者简化的 Lambda 表达式（没有大括号的）就需要进行类型推导：
         * 1. 是否是一个 void 函数，即 Consumer
         * 2. 是否是一个值返回函数，即 Function
         */
        /*
         * consumerIntFunction(Arrays::sort);
         * consumerIntFunction(data -> Arrays.sort(data));
         */

        consumerIntFunction((Consumer<int[]>) Arrays::sort);

        /*
         * 问题的关键：解析方法需要知道所需的方法签名，这应该通过目标类型确定，但是目标类型
         * 只有在泛型方法的类型参数已知是才能确定。
         * 从理论上来说，「方法重载解析」和「类型推断」可以同时进行，但实际上是先进行「方法重载解析」，
         * 再进行「类型推断」，因此「类型推断」得到的类型信息不能用于解决「方法重载解析」，最终导致
         * 编译报错。
         */
    }
}
