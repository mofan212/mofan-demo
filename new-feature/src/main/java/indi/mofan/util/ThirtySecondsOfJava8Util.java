package indi.mofan.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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

        /**
         * 扁平化一层
         */
        public static Object[] flatten(Object[] elements) {
            return Arrays.stream(elements)
                    .flatMap(el -> el instanceof Object[]
                            ? Arrays.stream((Object[]) el)
                            : Stream.of(el)
                    ).toArray();
        }

        /**
         * 全部扁平化
         */
        public static Object[] deepFlatten(Object[] input) {
            return Arrays.stream(input)
                    .flatMap(o -> {
                        if (o instanceof Object[]) {
                            return Arrays.stream(deepFlatten((Object[]) o));
                        }
                        return Stream.of(o);
                    }).toArray();
        }

        /**
         * 压缩到指定深度
         */
        public static Object[] flattenDepth(Object[] elements, int depth) {
            if (depth == 0) {
                return elements;
            }
            return Arrays.stream(elements)
                    .flatMap(el -> el instanceof Object[]
                            ? Arrays.stream(flattenDepth((Object[]) el, depth - 1))
                            : Arrays.stream(new Object[]{el})
                    ).toArray();
        }

        public static <T, R> Map<R, List<T>> groupBy(T[] elements, Function<T, R> func) {
            return Arrays.stream(elements).collect(Collectors.groupingBy(func));
        }

        /**
         * 移除最后一个元素
         */
        public static <T> T[] initial(T[] elements) {
            return Arrays.copyOfRange(elements, 0, elements.length - 1);
        }

        /**
         * 包含指定范围内的数字
         */
        public static int[] initializeArrayWithRange(int end, int start) {
            return IntStream.rangeClosed(start, end).toArray();
        }

        /**
         * 使用 n 个 value 生成数组
         */
        public static int[] initializeArrayWithValues(int n, int value) {
            return IntStream.generate(() -> value).limit(n).toArray();
        }

        /**
         * 交集
         */
        public static int[] intersection(int[] first, int[] second) {
            Set<Integer> set = Arrays.stream(first).boxed().collect(Collectors.toSet());
            return Arrays.stream(second).filter(set::contains).toArray();
        }

        /**
         * 给定数组是升序排序，返回 1；降序排序，返回 -1；否则返回 0
         */
        public static <T extends Comparable<? super T>> int isSorted(T[] arr) {
            // 长度小于 2，返回 0
            if (ArrayUtils.getLength(arr) < 2) {
                return 0;
            }
            final int direction = arr[0].compareTo(arr[1]) < 0 ? 1 : -1;
            for (int i = 1; i < arr.length; i++) {
                if (i == arr.length - 1) {
                    return direction;
                } else if ((arr[i].compareTo(arr[i + 1]) * direction > 0)) {
                    return 0;
                }
            }
            return direction;
        }
    }


}
