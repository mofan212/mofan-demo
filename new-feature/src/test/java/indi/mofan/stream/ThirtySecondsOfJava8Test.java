package indi.mofan.stream;

import indi.mofan.util.ThirtySecondsOfJava8Util;
import org.assertj.core.api.WithAssertions;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * @author mofan
 * @date 2023/9/7 20:11
 * @link <a href="https://github.com/hellokaton/30-seconds-of-java8">hellokaton/30-seconds-of-java8</a>
 */
public class ThirtySecondsOfJava8Test implements WithAssertions {
    @Test
    public void testChunk() {
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0);
        List<List<Integer>> chunk = ThirtySecondsOfJava8Util.Array.chunkWithObj(list, 3);
        assertThat(chunk).hasSize(4)
                .containsExactly(
                        List.of(1, 2, 3),
                        List.of(4, 5, 6),
                        List.of(7, 8, 9),
                        List.of(0)
                );

        int[] ints = list.stream().mapToInt(Integer::intValue).toArray();
        int[][] result = ThirtySecondsOfJava8Util.Array.chunkWithInt(ints, 3);
        assertThat(result).contains(new int[]{1, 2, 3}, Index.atIndex(0))
                .contains(new int[]{4, 5, 6}, Index.atIndex(1))
                .contains(new int[]{7, 8, 9}, Index.atIndex(2))
                .contains(new int[]{0}, Index.atIndex(3));
    }

    @Test
    public void testConcat() {
        Integer[] ints1 = {1, 2, 3};
        Integer[] ints2 = {4, 5, 6};
        Integer[] result = ThirtySecondsOfJava8Util.Array.concat(ints1, ints2);
        assertThat(result).hasSize(6).containsExactly(1, 2, 3, 4, 5, 6);
    }

    @Test
    public void testCountOccurrences() {
        int[] ints = {1, 2, 2, 3, 3, 3};
        long result = ThirtySecondsOfJava8Util.Array.countOccurrences(ints, 2);
        assertThat(result).isEqualTo(2);
    }

    @Test
    public void testDifference() {
        int[] ints1 = {1, 2, 3, 4};
        int[] ints2 = {2, 2, 3};
        int[] difference = ThirtySecondsOfJava8Util.Array.difference(ints1, ints2);
        assertThat(difference).hasSize(2).containsExactly(1, 4);
    }

    @Test
    public void testDifferenceWith() {
        int[] ints1 = {1, 2, 3, 4};
        int[] ints2 = {2, 2, 3, 3, 3, 4, 4, 4, 4};
        int[] result = ThirtySecondsOfJava8Util.Array.differenceWith(ints1, ints2, Math::subtractExact);
        assertThat(result).hasSize(1).containsExactly(1);
    }

    @Test
    public void testDistinctValuesOfArray() {
        int[] ints = {1, 2, 2, 3, 3, 3, 4, 4, 4, 4};
        int[] result = ThirtySecondsOfJava8Util.Array.distinctValuesOfArray(ints);
        assertThat(result).hasSize(4).containsExactly(1, 2, 3, 4);
    }

    @Test
    public void testDropElements() {
        int[] ints = {1, 2, 2, 3, 3, 3};
        int[] result = ThirtySecondsOfJava8Util.Array.dropElements(ints, value -> value == 3);
        assertThat(result).hasSize(3).containsOnly(3);

        result = ThirtySecondsOfJava8Util.Array.dropElements(ints, value -> value == 4);
        assertThat(result).isEmpty();
    }

    @Test
    public void testDropRight() {
        int[] ints = {1, 2, 3, 4, 5, 6};
        int[] result = ThirtySecondsOfJava8Util.Array.dropRight(ints, 3);
        assertThat(result).hasSize(3).containsExactly(1, 2, 3);
    }

    @Test
    public void testEveryNth() {
        int[] ints = {0, 1, 2, 3, 4, 5};
        int[] result = ThirtySecondsOfJava8Util.Array.everyNth(ints, 1);
        assertThat(result).hasSize(ints.length);

        result = ThirtySecondsOfJava8Util.Array.everyNth(ints, 2);
        assertThat(result).hasSize(3).containsExactly(1, 3, 5);

        result = ThirtySecondsOfJava8Util.Array.everyNth(ints, 3);
        assertThat(result).hasSize(2).containsExactly(2, 5);

        result = ThirtySecondsOfJava8Util.Array.everyNth(ints, 4);
        assertThat(result).hasSize(1).containsExactly(3);

        result = ThirtySecondsOfJava8Util.Array.everyNth(ints, 7);
        assertThat(result).isEmpty();
    }

    @Test
    public void testIndexOf() {
        int[] ints = {1, 2, 3};
        int index = ThirtySecondsOfJava8Util.Array.indexOf(ints, 2);
        assertThat(index).isEqualTo(1);
        index = ThirtySecondsOfJava8Util.Array.indexOf(ints, 0);
        assertThat(index).isEqualTo(-1);
    }

    @Test
    public void testLastIndexOf() {
        int[] ints = {1, 2, 3, 3, 4, 5};
        int lastIndexOf = ThirtySecondsOfJava8Util.Array.lastIndexOf(ints, 3);
        assertThat(lastIndexOf).isEqualTo(3);
    }

    @Test
    public void testFilterNonUnique() {
        int[] ints = {1, 2, 3, 2, 4, 3, 5, 5};
        int[] result = ThirtySecondsOfJava8Util.Array.filterNonUnique(ints);
        assertThat(result).hasSize(6)
                .containsExactly(2, 3, 2, 3, 5, 5);
    }

    private static final Object[] FLATTEN_ARRAY = {
            1,
            new Object[]{2, new Object[]{3}},
            new Object[]{4, new Object[]{5, new Object[]{6}}},
            new Object[]{7, new Object[]{8, new Object[]{9, new Object[]{0}}}}
    };

    @Test
    public void testFlatten() {
        Object[] result = ThirtySecondsOfJava8Util.Array.flatten(FLATTEN_ARRAY);
        assertThat(result).hasSize(7)
                .containsExactly(
                        1, 2, new Object[]{3},
                        4, new Object[]{5, new Object[]{6}},
                        7, new Object[]{8, new Object[]{9, new Object[]{0}}}
                );
    }

    @Test
    public void testDeepFlatten() {
        Object[] result = ThirtySecondsOfJava8Util.Array.deepFlatten(FLATTEN_ARRAY);
        assertThat(result).hasSize(10)
                .containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 0);
    }

    @Test
    public void testFlattenDepth() {
        Object[] result = ThirtySecondsOfJava8Util.Array.flattenDepth(FLATTEN_ARRAY, 2);
        assertThat(result).hasSize(9)
                .containsExactly(1, 2, 3, 4, 5, new Object[]{6}, 7, 8, new Object[]{9, new Object[]{0}});
    }

    @Test
    public void testGroupBy() {
        Integer[] integers = {1, 2, 3, 4, 5, 6};
        Map<Boolean, List<Integer>> group = ThirtySecondsOfJava8Util.Array.groupBy(integers, i -> i % 2 == 0);
        assertThat(group).hasSize(2)
                .contains(Map.entry(Boolean.TRUE, List.of(2, 4, 6)), Map.entry(Boolean.FALSE, List.of(1, 3, 5)));
    }

    @Test
    public void testInitial() {
        Integer[] integers = {1, 2, 3, 4};
        Integer[] result = ThirtySecondsOfJava8Util.Array.initial(integers);
        assertThat(result).containsExactly(1, 2, 3);
    }

    @Test
    public void testInitializeArrayWithRange() {
        int[] result = ThirtySecondsOfJava8Util.Array.initializeArrayWithRange(3, 1);
        assertThat(result).containsExactly(1, 2, 3);
    }

    @Test
    public void testInitializeArrayWithValues() {
        int[] result = ThirtySecondsOfJava8Util.Array.initializeArrayWithValues(3, 1);
        assertThat(result).containsExactly(1, 1, 1);
    }

    @Test
    public void testIntersection() {
        int[] ints1 = {1, 2, 3, 4};
        int[] ints2 = {3, 4, 5, 6};
        int[] intersection = ThirtySecondsOfJava8Util.Array.intersection(ints1, ints2);
        assertThat(intersection).containsExactly(3, 4);
    }

    @Test
    public void testIsSorted() {
        Integer[] ints = {1};
        int sorted = ThirtySecondsOfJava8Util.Array.isSorted(ints);
        assertThat(sorted).isEqualTo(0);

        ints = new Integer[]{1, 2, 1};
        sorted = ThirtySecondsOfJava8Util.Array.isSorted(ints);
        assertThat(sorted).isEqualTo(0);

        ints = new Integer[]{1, 2, 3, 4};
        sorted = ThirtySecondsOfJava8Util.Array.isSorted(ints);
        assertThat(sorted).isEqualTo(1);
    }

    @Test
    public void testJoin() {
        Integer[] ints = {1, 2, 3, 4, 5};
        String end = ".";
        String result = ThirtySecondsOfJava8Util.Array.join(ints, ",", end);
        assertThat(result).isEqualTo("1,2,3,4.5");

        result = ThirtySecondsOfJava8Util.Array.joinToString(ints, ",", end);
        assertThat(result).isEqualTo("1,2,3,4,5.");
    }

    @Test
    public void testNthElement() {
        Integer[] integers = {1, 2, 3, 4, 5};
        Integer element = ThirtySecondsOfJava8Util.Array.nthElement(integers, 2);
        assertThat(element).isEqualTo(3);

        element = ThirtySecondsOfJava8Util.Array.nthElement(integers, -1);
        assertThat(element).isEqualTo(5);

        assertThatExceptionOfType(ArrayIndexOutOfBoundsException.class)
                .isThrownBy(() -> ThirtySecondsOfJava8Util.Array.nthElement(integers, -6));
    }

    @Test
    public void testPick() {
        Integer[] integers = {2, 4, 6};
        Map<Integer, Integer> map = Map.of(1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6);
        Map<Integer, Integer> result = ThirtySecondsOfJava8Util.Array.pick(map, integers);
        assertThat(result).hasSize(3)
                .containsExactly(Map.entry(2, 2), Map.entry(4, 4), Map.entry(6, 6));
    }
}
