package indi.mofan;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

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

    @Test
    public void testMapMulti() {
        List<String> list = List.of("Tom", "Jerry");
        // 将一个转换成多个
        List<String> result = list.stream().<String>mapMulti((s, c) -> {
            // s 的全大写、全小写会添加到新的流中
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

    @Test
    public void testTakeWhile() {
        List<Integer> list = Stream.of(1, 2, 3, 7, 8, 9, 4, 5, 6)
                .takeWhile(i -> i <= 6)
                .toList();
        // 只要不大于 6 的，碰到大于 6 的剩下的都不要
        assertThat(list).containsExactly(1, 2, 3);
    }

    @Test
    public void testDropWhile() {
        List<Integer> list = Stream.of(1, 2, 3, 7, 8, 9, 4, 5, 6)
                .dropWhile(i -> i <= 6)
                .toList();
        // 不大于 6 的都不要，一碰到大于 6 的剩下都要
        assertThat(list).containsExactly(7, 8, 9, 4, 5, 6);
    }

    @Test
    public void testMatch() {
        List<String> list = new ArrayList<>();
        assertThat(list.stream().allMatch("test"::equals)).isTrue();
        assertThat(list.stream().allMatch(i -> !"test".equals(i))).isTrue();
        assertThat(list.stream().noneMatch("test"::equals)).isTrue();
        assertThat(list.stream().noneMatch(i -> !"test".equals(i))).isTrue();
        assertThat(list.stream().anyMatch(i -> !"test".equals(i))).isFalse();
        assertThat(list.stream().anyMatch("test"::equals)).isFalse();
    }
}
