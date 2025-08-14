package indi.mofan;

import com.google.common.collect.Lists;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author mofan
 * @date 2022/10/10 22:17
 */
public class CurryingTest implements WithAssertions {

    @FunctionalInterface
    interface Fa {
        Fb op(List<Integer> list);
    }

    @FunctionalInterface
    interface Fb {
        Fc op(List<Integer> list);
    }

    @FunctionalInterface
    interface Fc {
        List<Integer> op(List<Integer> list);
    }

    Fb step1() {
        List<Integer> a = Lists.newArrayList(1, 2, 3);
        Fa fa = i -> j -> k -> {
            List<Integer> list = new ArrayList<>(i);
            list.addAll(j);
            list.addAll(k);
            return list;
        };
        return fa.op(a);
    }

    Fc step2(Fb fb) {
        List<Integer> b = Lists.newArrayList(4, 5, 6);
        return fb.op(b);
    }

    List<Integer> step3(Fc fc) {
        List<Integer> c = Lists.newArrayList(7, 8, 9);
        return fc.op(c);
    }

    @Test
    public void testCurrying() {
        List<Integer> list = step3(step2(step1()));
        assertThat(list).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    /**
     * <p>
     * 命名参考：
     * <ul>
     *     <li>Unary:      一元</li>
     *     <li>Binary:     二元</li>
     *     <li>Ternary:    三元</li>
     *     <li>Quaternary: 四元</li>
     * </ul>
     * </p>
     */
    @FunctionalInterface
    interface TernaryConsumer<T, U, R> {
        void accept(T t, U u, R r);
    }

    @Test
    public void testTernaryConsumer() {
        TernaryConsumer<Double, Float, Integer> consumer = (t, u, r) -> {
            System.out.println(t + ";" + u + ";" + r);
        };
        // 1.0;2.0;3
        consumer.accept(1.0, 2f, 3);

        Function<Double, BiConsumer<Float, Integer>> fun = t -> (u, r) -> {
            System.out.println(t + ";" + u + ";" + r);
        };
        // 1.0;2.0;3
        fun.apply(1.0).accept(2f, 3);

        Function<Double, Function<Float, Consumer<Integer>>> f = t -> u -> r -> {
            System.out.println(t + ";" + u + ";" + r);
        };
        // 1.0;2.0;3
        f.apply(1.0).apply(2f).accept(3);

        BiFunction<Double, Float, Consumer<Integer>> biFun = (t, u) -> r -> {
            System.out.println(t + ";" + u + ";" + r);
        };
        // 1.0;2.0;3
        biFun.apply(1.0, 2f).accept(3);
    }
}
