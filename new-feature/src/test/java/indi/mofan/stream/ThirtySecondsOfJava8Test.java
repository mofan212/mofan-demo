package indi.mofan.stream;

import indi.mofan.util.ThirtySecondsOfJava8Util;
import org.assertj.core.api.WithAssertions;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author mofan
 * @date 2023/9/7 20:11
 * @link <a href="https://github.com/hellokaton/30-seconds-of-java8">hellokaton/30-seconds-of-java8</a>
 */
public class ThirtySecondsOfJava8Test implements WithAssertions {
    @Test
    public void testChunk() {
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0);
        List<List<Integer>> chunk = ThirtySecondsOfJava8Util.chunkWithObj(list, 3);
        assertThat(chunk).hasSize(4)
                .containsExactly(
                        List.of(1, 2, 3),
                        List.of(4, 5, 6),
                        List.of(7, 8, 9),
                        List.of(0)
                );

        int[] ints = list.stream().mapToInt(Integer::intValue).toArray();
        int[][] result = ThirtySecondsOfJava8Util.chunkWithInt(ints, 3);
        assertThat(result).contains(new int[]{1, 2, 3}, Index.atIndex(0))
                .contains(new int[]{4, 5, 6}, Index.atIndex(1))
                .contains(new int[]{7, 8, 9}, Index.atIndex(2))
                .contains(new int[]{0}, Index.atIndex(3));
    }
}
