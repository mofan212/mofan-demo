package indi.mofan.stream;

import indi.mofan.util.ThirtySecondsOfJava8Util;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.api.WithAssertions;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.stream.Stream;

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

    public record AggObject(int value, List<AggObject> children) {
        public AggObject(int value) {
            this(value, List.of());
        }
    }

    private List<AggObject> deepFlattenAggObjectList(List<AggObject> aggObjectList) {
        return CollectionUtils.emptyIfNull(aggObjectList).stream()
                .flatMap(i -> {
                    List<AggObject> children = i.children();
                    if (CollectionUtils.isNotEmpty(children)) {
                        return Stream.concat(deepFlattenAggObjectList(children).stream(), Stream.of(i));
                    }
                    return Stream.of(i);
                }).toList();
    }

    @Test
    public void testDeepFlattenAggObjectList() {
        List<AggObject> list = List.of(
                new AggObject(1),
                new AggObject(2, List.of(new AggObject(3), new AggObject(4))),
                new AggObject(5, List.of(new AggObject(6, List.of(new AggObject(7)))))
        );
        List<Integer> intValue = deepFlattenAggObjectList(list).stream()
                .map(AggObject::value)
                .toList();
        assertThat(intValue).hasSize(7)
                .containsExactlyInAnyOrder(1, 2, 3, 4, 5, 6, 7);
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

    @Test
    public void testReducedFilter() {
        @SuppressWarnings({"unchecked"})
        Map<String, Object>[] maps = (Map<String, Object>[]) Array.newInstance(Map.class, 5);
        for (int i = 0; i < 5; i++) {
            int plusOne = i + 1;
            maps[i] = Map.of(String.valueOf(plusOne), plusOne);
        }

        Map<String, Object>[] result = ThirtySecondsOfJava8Util.Array.reducedFilter(maps, new String[]{"2", "4"},
                map -> map.values().stream().anyMatch(value -> value instanceof Integer integer && integer > 3));
        assertThat(result).hasSize(1)
                .containsExactly(Map.of("4", 4));
    }

    @RepeatedTest(10)
    public void testSample() {
        Integer[] integers = {1, 2, 3, 4, 5};
        Integer sample = ThirtySecondsOfJava8Util.Array.sample(integers);
        assertThat(sample).isBetween(1, 5);
    }

    @RepeatedTest(10)
    public void testShuffle() {
        Integer[] integers = {1, 2, 3, 4, 5};
        Integer[] shuffle = ThirtySecondsOfJava8Util.Array.shuffle(integers);
        assertThat(shuffle).hasSize(5)
                .doesNotHaveDuplicates()
                .containsExactlyInAnyOrder(integers);
    }

    @RepeatedTest(10)
    public void testSampleSize() {
        Integer[] integers = {1, 2, 3, 4, 5};
        Integer[] result = ThirtySecondsOfJava8Util.Array.sampleSize(integers, 3);
        assertThat(result).hasSize(3)
                .doesNotHaveDuplicates()
                .containsAnyOf(integers);
    }

    @Test
    public void testSimilarity() {
        Integer[] ints1 = {1, 2, 3, 4};
        Integer[] ints2 = {3, 4, 5, 6};
        Integer[] similarity = ThirtySecondsOfJava8Util.Array.similarity(ints1, ints2);
        assertThat(similarity).containsExactly(3, 4);
    }

    @Test
    public void testSortedIndex() {
        Integer[] integers = {};
        int index = ThirtySecondsOfJava8Util.Array.sortedIndex(integers, 3);
        assertThat(index).isEqualTo(0);

        integers = new Integer[]{1, 2, 3, 4, 5};
        index = ThirtySecondsOfJava8Util.Array.sortedIndex(integers, 3);
        assertThat(index).isEqualTo(2);

        integers = new Integer[]{4, 2, 1};
        index = ThirtySecondsOfJava8Util.Array.sortedIndex(integers, 3);
        assertThat(index).isEqualTo(1);
    }

    @Test
    public void testSymmetricDifference() {
        Integer[] first = {1, 2, 3, 4, 5};
        Integer[] second = {3, 4, 5, 6, 7};
        Integer[] symmetricDifference = ThirtySecondsOfJava8Util.Array.symmetricDifference(first, second);
        assertThat(symmetricDifference).hasSize(4)
                .containsExactly(1, 2, 6, 7);
    }

    @Test
    public void testTail() {
        Integer[] integers = {1};
        Integer[] tail = ThirtySecondsOfJava8Util.Array.tail(integers);
        assertThat(tail).hasSize(1).containsOnly(1);

        integers = new Integer[]{1, 2, 3, 4};
        tail = ThirtySecondsOfJava8Util.Array.tail(integers);
        assertThat(tail).hasSize(3).containsExactly(2, 3, 4);
    }

    @Test
    public void testTake() {
        Integer[] integers = {1};
        Integer[] take = ThirtySecondsOfJava8Util.Array.take(integers, 2);
        assertThat(take).hasSize(2).containsExactly(1, null);

        integers = new Integer[]{1, 2, 3, 4};
        take = ThirtySecondsOfJava8Util.Array.take(integers, 2);
        assertThat(take).hasSize(2).containsExactly(1, 2);
    }

    @Test
    public void testTakeRight() {
        assertThatExceptionOfType(ArrayIndexOutOfBoundsException.class)
                .isThrownBy(() -> ThirtySecondsOfJava8Util.Array.takeRight(new Integer[]{1}, 2));

        Integer[] integers = new Integer[]{1, 2, 3};
        Integer[] result = ThirtySecondsOfJava8Util.Array.takeRight(integers, 2);
        assertThat(result).hasSize(2).containsExactly(2, 3);
    }

    @Test
    public void testUnion() {
        Integer[] integers1 = {1, 2, 3};
        Integer[] integers2 = {2, 3, 4};
        Integer[] union = ThirtySecondsOfJava8Util.Array.union(integers1, integers2);
        assertThat(union).hasSize(4)
                .doesNotHaveDuplicates()
                .containsExactly(1, 2, 3, 4);
    }

    @Test
    public void testWithout() {
        Integer[] integers = {1, 2, 3, 4, 5, 6};
        Integer[] result = ThirtySecondsOfJava8Util.Array.without(integers, 2, 4, 6);
        assertThat(result).hasSize(3)
                .containsExactly(1, 3, 5);
    }

    @Test
    public void testZip() {
        Integer[] integers1 = {1, 2, 3};
        Integer[] integers2 = {4, 5, 6};
        List<Object[]> zip = ThirtySecondsOfJava8Util.Array.zip(integers1, integers2);
        assertThat(zip).hasSize(3)
                .containsExactly(
                        new Object[]{1, 4},
                        new Object[]{2, 5},
                        new Object[]{3, 6}
                );

        integers1 = new Integer[]{1, 2, 3, 4, 5};
        integers2 = new Integer[]{4, 5, 6};
        zip = ThirtySecondsOfJava8Util.Array.zip(integers1, integers2);
        assertThat(zip).hasSize(5)
                .containsExactly(
                        new Object[]{1, 4},
                        new Object[]{2, 5},
                        new Object[]{3, 6},
                        new Object[]{4, null},
                        new Object[]{5, null}
                );
    }

    @Test
    public void testZipObject() {
        String[] strings = {"a", "b", "c"};
        Object[] objects = {1, 2, 3};
        Map<String, Object> map = ThirtySecondsOfJava8Util.Array.zipObject(strings, objects);
        assertThat(map).hasSize(3)
                .contains(
                        entry("a", 1),
                        entry("b", 2),
                        entry("c", 3)
                );

        objects = new Object[]{1, 2};
        map = ThirtySecondsOfJava8Util.Array.zipObject(strings, objects);
        assertThat(map).hasSize(3)
                .contains(
                        entry("a", 1),
                        entry("b", 2),
                        entry("c", null)
                );
    }

    @Test
    public void testAverage() {
        int[] ints = {1, 2, 3};
        OptionalDouble optional = ThirtySecondsOfJava8Util.Maths.average(ints);
        assertThat(optional).hasValue(2);

        optional = ThirtySecondsOfJava8Util.Maths.average(new int[0]);
        assertThat(optional).isEmpty();
    }

    @Test
    public void testGcd() {
        int[] ints = {2, 4, 6};
        OptionalInt optionalInt = ThirtySecondsOfJava8Util.Maths.gcd(ints);
        assertThat(optionalInt).hasValue(2);

        optionalInt = ThirtySecondsOfJava8Util.Maths.gcd(new int[0]);
        assertThat(optionalInt).isEmpty();
    }

    @Test
    public void testLcm() {
        int[] ints = {1, 2, 3};
        OptionalInt optionalInt = ThirtySecondsOfJava8Util.Maths.lcm(ints);
        assertThat(optionalInt).hasValue(6);

        optionalInt = ThirtySecondsOfJava8Util.Maths.lcm(new int[0]);
        assertThat(optionalInt).isEmpty();
    }

    @Test
    public void testFindNextPositivePowerOfTwo() {
        int nextPositivePowerOfTwo = ThirtySecondsOfJava8Util.Maths.findNextPositivePowerOfTwo(2);
        assertThat(nextPositivePowerOfTwo).isEqualTo(2);

        nextPositivePowerOfTwo = ThirtySecondsOfJava8Util.Maths.findNextPositivePowerOfTwo(8);
        assertThat(nextPositivePowerOfTwo).isEqualTo(8);

        nextPositivePowerOfTwo = ThirtySecondsOfJava8Util.Maths.findNextPositivePowerOfTwo(10);
        assertThat(nextPositivePowerOfTwo).isEqualTo(16);
    }

    @Test
    public void testIsEven() {
        boolean isEven = ThirtySecondsOfJava8Util.Maths.isEven(1);
        assertThat(isEven).isFalse();

        isEven = ThirtySecondsOfJava8Util.Maths.isEven(100);
        assertThat(isEven).isTrue();
    }

    @Test
    public void testIsPowerOfTwo() {
        boolean isPowerOfTwo = ThirtySecondsOfJava8Util.Maths.isPowerOfTwo(0);
        assertThat(isPowerOfTwo).isFalse();

        isPowerOfTwo = ThirtySecondsOfJava8Util.Maths.isPowerOfTwo(1);
        assertThat(isPowerOfTwo).isTrue();

        isPowerOfTwo = ThirtySecondsOfJava8Util.Maths.isPowerOfTwo(6);
        assertThat(isPowerOfTwo).isFalse();
    }

    @RepeatedTest(10)
    public void testGenerateRandomInt() {
        int randomInt = ThirtySecondsOfJava8Util.Maths.generateRandomInt();
        assertThat(randomInt).isBetween(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Test
    public void testAnagrams() {
        List<String> anagrams = ThirtySecondsOfJava8Util.Strings.anagrams("");
        assertThat(anagrams).hasSize(1).containsOnly("");

        anagrams = ThirtySecondsOfJava8Util.Strings.anagrams("ab");
        assertThat(anagrams).hasSize(2).containsExactly("ab", "ba");

        anagrams = ThirtySecondsOfJava8Util.Strings.anagrams("abc");
        assertThat(anagrams).containsExactly("abc", "acb", "bac", "bca", "cab", "cba");
    }

    @Test
    public void testByteSize() {
        int byteSize = ThirtySecondsOfJava8Util.Strings.byteSize("abc");
        assertThat(byteSize).isEqualTo(3);

        byteSize = ThirtySecondsOfJava8Util.Strings.byteSize("默烦");
        assertThat(byteSize).isEqualTo(6);
    }

    @Test
    public void testCapitalize() {
        String capitalize = ThirtySecondsOfJava8Util.Strings.capitalize("aBC", false);
        assertThat(capitalize).isEqualTo("ABC");

        capitalize = ThirtySecondsOfJava8Util.Strings.capitalize("aBC", true);
        assertThat(capitalize).isEqualTo("Abc");
    }

    @Test
    public void testCapitalizeEveryWord() {
        String string = ThirtySecondsOfJava8Util.Strings.capitalizeEveryWord("hello");
        assertThat(string).isEqualTo("Hello");

        string = ThirtySecondsOfJava8Util.Strings.capitalizeEveryWord("hello world!");
        assertThat(string).isEqualTo("Hello World!");
    }

    @Test
    public void testCountVowels() {
        int count = ThirtySecondsOfJava8Util.Strings.countVowels("Hello World");
        assertThat(count).isEqualTo(3);

        count = ThirtySecondsOfJava8Util.Strings.countVowels("默烦");
        assertThat(count).isEqualTo(0);
    }

    @Test
    public void testEscapeRegExp() {
        String exp = ThirtySecondsOfJava8Util.Strings.escapeRegExp("\\b(?=\\w");
        assertThat(exp).isEqualTo("\\Q\\b(?=\\w\\E");
    }

    @Test
    public void testFromCamelCase() {
        String str = ThirtySecondsOfJava8Util.Strings.fromCamelCase("HelloWorld", ",");
        assertThat(str).isEqualTo("hello,world");

        str = ThirtySecondsOfJava8Util.Strings.fromCamelCase("helloWorld", ",");
        assertThat(str).isEqualTo("hello,world");
    }

    @Test
    public void testIsAbsoluteUrl() {
        boolean isAbsoluteUrl = ThirtySecondsOfJava8Util.Strings.isAbsoluteUrl("https://github.com/");
        assertThat(isAbsoluteUrl).isTrue();

        isAbsoluteUrl = ThirtySecondsOfJava8Util.Strings.isAbsoluteUrl("github.com");
        assertThat(isAbsoluteUrl).isFalse();
    }

    @Test
    public void testIsLowerCase() {
        boolean lowerCase = ThirtySecondsOfJava8Util.Strings.isLowerCase("hellO");
        assertThat(lowerCase).isFalse();

        lowerCase = ThirtySecondsOfJava8Util.Strings.isLowerCase("hello");
        assertThat(lowerCase).isTrue();
    }

    @Test
    public void testIsUpperCase() {
        boolean upperCase = ThirtySecondsOfJava8Util.Strings.isUpperCase("HELLo");
        assertThat(upperCase).isFalse();

        upperCase = ThirtySecondsOfJava8Util.Strings.isUpperCase("HELLO");
        assertThat(upperCase).isTrue();
    }

    @Test
    public void testIsPalindrome() {
        boolean palindrome = ThirtySecondsOfJava8Util.Strings.isPalindrome("hello hello");
        assertThat(palindrome).isFalse();

        // 回文：颠倒后等于原字符串
        palindrome = ThirtySecondsOfJava8Util.Strings.isPalindrome("hello olleh");
        assertThat(palindrome).isTrue();
    }

    @Test
    public void testIsNumeric() {
        boolean numeric = ThirtySecondsOfJava8Util.Strings.isNumeric("hello");
        assertThat(numeric).isFalse();

        numeric = ThirtySecondsOfJava8Util.Strings.isNumeric("123");
        assertThat(numeric).isTrue();
    }

    @Test
    public void testMask() {
        String mask = ThirtySecondsOfJava8Util.Strings.mask("Hello World", 5, "*");
        assertThat(mask).isEqualTo("******World");

        mask = ThirtySecondsOfJava8Util.Strings.mask("Hello World", -5, "*");
        assertThat(mask).isEqualTo("Hello******");
    }

    @Test
    public void testReverse() {
        String reverse = ThirtySecondsOfJava8Util.Strings.reverse("abc");
        assertThat(reverse).isEqualTo("cba");
    }

    @Test
    public void testSortCharactersInString() {
        String string = ThirtySecondsOfJava8Util.Strings.sortCharactersInString("cba");
        assertThat(string).isEqualTo("abc");
    }

    @Test
    public void testSplitLines() {
        String str = """
                abc
                xyz
                """;
        String[] lines = ThirtySecondsOfJava8Util.Strings.splitLines(str);
        assertThat(lines).hasSize(2)
                .containsExactly("abc", "xyz");
    }

    @Test
    public void testToCamelCase() {
        String camelCase = ThirtySecondsOfJava8Util.Strings.toCamelCase("hello world");
        assertThat(camelCase).isEqualTo("helloWorld");

        camelCase = ThirtySecondsOfJava8Util.Strings.toCamelCase(" getA ");
        assertThat(camelCase).isEqualTo("getA");
    }

    @Test
    public void testToKebabCase() {
        String kebabCase = ThirtySecondsOfJava8Util.Strings.toKebabCase("hello world");
        assertThat(kebabCase).isEqualTo("hello-world");

        kebabCase = ThirtySecondsOfJava8Util.Strings.toCamelCase(" getA ");
        assertThat(kebabCase).isEqualTo("getA");
    }

    @Test
    public void testMatch() {
        String regex = "[A-Z]{2,}(?=[A-Z][a-z]+[0-9]*|\\b)|[A-Z]?[a-z]+[0-9]*|[A-Z]|[0-9]+";
        List<String> list = ThirtySecondsOfJava8Util.Strings.match("hello world", regex);
        assertThat(list).hasSize(2).containsExactly("hello", "world");
    }

    @Test
    public void testToSnakeCase() {
        String snakeCase = ThirtySecondsOfJava8Util.Strings.toSnakeCase("hello world");
        assertThat(snakeCase).isEqualTo("hello_world");

        snakeCase = ThirtySecondsOfJava8Util.Strings.toCamelCase(" getA ");
        assertThat(snakeCase).isEqualTo("getA");
    }

    @Test
    public void testTruncate() {
        String truncate = ThirtySecondsOfJava8Util.Strings.truncate("hello world", 8);
        assertThat(truncate).isEqualTo("hello...");

        truncate = ThirtySecondsOfJava8Util.Strings.truncate("hello world", 100);
        assertThat(truncate).isEqualTo("hello world");
    }

    @Test
    public void testWords() {
        String[] words = ThirtySecondsOfJava8Util.Strings.words("hello world");
        assertThat(words).containsExactly("hello", "world");
    }

    @Test
    public void testStringToIntegers() {
        int[] ints = ThirtySecondsOfJava8Util.Strings.stringToIntegers("12 34 56");
        assertThat(ints).containsExactly(12, 34, 56);

        assertThatExceptionOfType(NumberFormatException.class)
                .isThrownBy(() -> ThirtySecondsOfJava8Util.Strings.stringToIntegers("mofan"));
    }

    private static final ClassPathResource RESOURCE = new ClassPathResource("hello-world.txt");

    @Test
    @SneakyThrows
    public void testConvertInputStreamToString() {
        InputStream inputStream = RESOURCE.getInputStream();
        String string = ThirtySecondsOfJava8Util.IO.convertInputStreamToString(inputStream);
        assertThat(string).isEqualTo("Hello World!");
    }

    @Test
    @SneakyThrows
    public void testReadFileAsString() {
        Path path = Path.of(RESOURCE.getURI());
        String content = ThirtySecondsOfJava8Util.IO.readFileAsString(path);
        assertThat(content).isEqualTo("Hello World!");
    }

    @Test
    public void testGetCurrentWorkingDirectoryPath() {
        String path = ThirtySecondsOfJava8Util.IO.getCurrentWorkingDirectoryPath();
        assertThat(path).endsWith("new-feature\\other");
        File file = new File(path);
        assertThat(file).exists().isDirectory();
    }

    @Test
    public void testTmpDirName() {
        String tmpDirName = ThirtySecondsOfJava8Util.IO.tmpDirName();
        File file = new File(tmpDirName);
        assertThat(file).exists().isDirectory();
    }

    @Test
    @SuppressWarnings("all")
    public void testStackTraceAsString() {
        try {
            int i = 1 / 0;
            Assertions.fail();
        } catch (Exception e) {
            String stackTrace = ThirtySecondsOfJava8Util.Exceptions.stackTraceAsString(e);
            assertThat(stackTrace).startsWith("java.lang.ArithmeticException: / by zero");
        }
    }

    @Test
    public void testOsName() {
        String osName = ThirtySecondsOfJava8Util.Systems.osName();
        assertThat(osName).isNotEmpty();
    }

    interface InterfaceA {
    }

    interface InterfaceB {
    }

    static abstract class AbstractA implements InterfaceA {
    }

    static class TargetA extends AbstractA implements InterfaceB {
        @SuppressWarnings("all")
        private class InnerClass {
        }
    }

    @Test
    public void testGetAllInterfaces() {
        List<Class<?>> interfaces = ThirtySecondsOfJava8Util.Clazz.getAllInterfaces(TargetA.class);
        assertThat(interfaces).hasSize(2)
                .containsExactlyInAnyOrder(InterfaceA.class, InterfaceB.class);
    }

    @Test
    public void testIsInnerClass() {
        boolean innerClass = ThirtySecondsOfJava8Util.Clazz.isInnerClass(TargetA.class);
        assertThat(innerClass).isTrue();
        TargetA targetA = new TargetA();
        TargetA.InnerClass inner = targetA.new InnerClass();
        innerClass = ThirtySecondsOfJava8Util.Clazz.isInnerClass(inner.getClass());
        assertThat(innerClass).isTrue();
        innerClass = ThirtySecondsOfJava8Util.Clazz.isInnerClass(this.getClass());
        assertThat(innerClass).isFalse();
    }

    enum MyEnum {
        ONE, TWO
    }

    @Test
    public void testGetEnumMap() {
        Map<String, MyEnum> enumMap = ThirtySecondsOfJava8Util.Enums.getEnumMap(MyEnum.class);
        assertThat(enumMap).hasSize(2)
                .contains(entry(MyEnum.ONE.name(), MyEnum.ONE), entry(MyEnum.TWO.name(), MyEnum.TWO));
    }
}
