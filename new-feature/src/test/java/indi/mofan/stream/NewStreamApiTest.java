package indi.mofan.stream;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author mofan
 * @date 2023/11/16 21:10
 */
public class NewStreamApiTest implements WithAssertions {

    private void deepFlatten(Object e, Consumer<Object> c) {
        if (e instanceof Iterable<?> elements) {
            elements.forEach(ie -> deepFlatten(ie, c));
        } else if (e != null) {
            c.accept(e);
        }
    }

    /**
     * @see indi.mofan.util.ThirtySecondsOfJava8Util.Array#deepFlatten(Object[])
     */
    @Test
    public void testMapMulti() {
        List<String> list = List.of("Tom", "Jerry");
        // 将一个转换成多个
        List<String> result = list.stream().<String>mapMulti((s, c) -> {
            c.accept(s.toUpperCase());
            c.accept(s.toLowerCase());
        }).toList();
        assertThat(result).hasSize(4)
                .containsExactlyInAnyOrder("TOM", "tom", "JERRY", "jerry");

        // 深度扁平化
        var nestedList = List.of(1, 2, List.of(3, 4, List.of(5, 6)), List.of(7, 8));
        List<Integer> integerList = nestedList.stream()
                .mapMulti(this::deepFlatten)
                .map(String::valueOf)
                .map(Integer::valueOf)
                .toList();
        assertThat(integerList).hasSize(8)
                .hasSizeBetween(1, 8);
    }
}
