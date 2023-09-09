package indi.mofan.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.IntBinaryOperator;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author mofan
 * @date 2023/9/7 20:21
 */
public class ThirtySecondsOfJava8Util {

    public final static class Array {
        /**
         * 按给定 size 进行分块
         */
        public static <T> List<List<T>> chunkWithObj(List<T> nums, int size) {
            if (CollectionUtils.isEmpty(nums)) {
                return Collections.emptyList();
            }
            return IntStream.iterate(0, i -> i + size)
                    // ceil 向上取整，确定有多少组
                    .limit((long) Math.ceil((double) nums.size() / size))
                    .mapToObj(i -> nums.subList(i, Math.min(i + size, nums.size())))
                    .toList();
        }

        public static int[][] chunkWithInt(int[] nums, int size) {
            if (ArrayUtils.isEmpty(nums)) {
                return new int[0][0];
            }
            return IntStream.iterate(0, i -> i + size)
                    .limit((long) Math.ceil((double) nums.length / size))
                    .mapToObj(i -> Arrays.copyOfRange(nums, i, Math.min(i + size, nums.length)))
                    .toArray(int[][]::new);
        }

        @SuppressWarnings("unchecked")
        public static <T> T[] concat(T[] first, T[] second) {
            return Stream.concat(Stream.of(first), Stream.of(second))
                    .toArray(i -> (T[]) Arrays.copyOf(new Object[0], i, first.getClass()));
        }

        public static long countOccurrences(int[] numbers, int value) {
            return Arrays.stream(numbers)
                    .filter(i -> i == value)
                    .count();
        }

        /**
         * 从 first 中找出不包含 second 的元素组成的数组
         */
        public static int[] difference(int[] first, int[] second) {
            Set<Integer> secondSet = Arrays.stream(second).boxed().collect(Collectors.toSet());
            return Arrays.stream(first).filter(i -> !secondSet.contains(i)).toArray();
        }

        /**
         * comparator == 0 表示相等
         */
        public static int[] differenceWith(int[] first, int[] second, IntBinaryOperator comparator) {
            return Arrays.stream(first)
                    .filter(i -> Arrays.stream(second).noneMatch(j -> comparator.applyAsInt(i, j) == 0))
                    .toArray();
        }

        /**
         * 去重
         */
        public static int[] distinctValuesOfArray(int[] elements) {
            return Arrays.stream(elements).distinct().toArray();
        }

        /**
         * 删除元素，直到 condition == true
         */
        public static int[] dropElements(int[] elements, IntPredicate condition) {
            return IntStream.range(0, elements.length)
                    .filter(i -> condition.test(elements[i]))
                    .findFirst()
                    .stream()
                    .mapToObj(i -> Arrays.copyOfRange(elements, i, elements.length))
                    .findFirst()
                    .orElse(new int[0]);
        }

        /**
         * 从右边移除 n 个元素
         */
        public static int[] dropRight(int[] elements, int n) {
            if (n < 0) {
                throw new IllegalArgumentException("n is less than 0");
            }
            return n < elements.length ? Arrays.copyOfRange(elements, 0, elements.length - n) : new int[0];
        }

        /**
         * 每个第 n 个元素
         */
        public static int[] everyNth(int[] elements, int nth) {
            return IntStream.range(0, elements.length)
                    .filter(i -> i % nth == nth - 1)
                    .map(i -> elements[i])
                    .toArray();
        }

        public static int indexOf(int[] elements, int el) {
            return IntStream.range(0, elements.length)
                    .filter(i -> elements[i] == el)
                    .findFirst()
                    .orElse(-1);
        }

        public static int lastIndexOf(int[] elements, int el) {
            return IntStream.iterate(elements.length - 1, i -> i - 1)
                    .limit(elements.length)
                    .filter(i -> elements[i] == el)
                    .findFirst()
                    .orElse(-1);
        }

        /**
         * 筛选出非唯一值，即出现了多次的值
         */
        public static int[] filterNonUnique(int[] elements) {
            return Arrays.stream(elements)
                    // 第一次出现与最后一次出现在不同的位置，证明非唯一
                    .filter(i -> indexOf(elements, i) != lastIndexOf(elements, i))
                    .toArray();
        }
    }


}
