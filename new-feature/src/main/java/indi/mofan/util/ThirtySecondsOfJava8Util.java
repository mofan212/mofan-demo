package indi.mofan.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
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

        /**
         * 将数组按照指定分隔符进行拼接，在倒数第二个元素时使用 end 分割
         */
        public static <T> String join(T[] arr, String separator, String end) {
            return IntStream.range(0, arr.length)
                    .mapToObj(i -> Map.entry(i, arr[i]))
                    .reduce("", (acc, val) -> val.getKey() == arr.length - 2
                            ? acc + val.getValue() + end
                            : val.getKey() == arr.length - 1 ? acc + val.getValue() : acc + val.getValue() + separator, (fst, snd) -> fst);
        }

        /**
         * 以指定字符串进行拼接，最后以 end 结尾
         */
        public static <T> String joinToString(T[] arr, String separator, String end) {
            return Arrays.stream(arr).map(String::valueOf)
                    .collect(Collectors.joining(separator, "", end));
        }

        /**
         * 第几个元素，正数按索引，负数倒序按位次
         */
        public static <T> T nthElement(T[] arr, int n) {
            if (n > 0) {
                return Arrays.copyOfRange(arr, n, arr.length)[0];
            }
            return Arrays.copyOfRange(arr, arr.length + n, arr.length)[0];
        }

        /**
         * 包含指定键的所有键值对
         */
        public static <T, R> Map<T, R> pick(Map<T, R> map, T[] keys) {
            return Arrays.stream(keys)
                    .filter(map::containsKey)
                    .collect(Collectors.toMap(Function.identity(), map::get));
        }

        /**
         * 按 fn 进行过滤，筛选出每个 Map 中包含 keys 的
         */
        public static Map<String, Object>[] reducedFilter(Map<String, Object>[] data,
                                                          String[] keys,
                                                          Predicate<Map<String, Object>> fn) {
            return Arrays.stream(data)
                    .map(map -> Arrays.stream(keys)
                            .filter(map::containsKey)
                            .collect(Collectors.toMap(Function.identity(), map::get)))
                    .filter(fn)
                    .toArray((IntFunction<Map<String, Object>[]>) Map[]::new);
        }

        /**
         * 随机返回数组中的一个元素
         */
        public static <T> T sample(T[] arr) {
            /*
             * 0 <= Math.random() < 1
             * Math.floor() 返回小于等于给定值的最大值
             */
            return arr[(int) Math.floor(Math.random() * arr.length)];
        }

        /**
         * 打乱原数组中的元素顺序
         */
        public static <T> T[] shuffle(T[] input) {
            int length = input.length;
            T[] arr = Arrays.copyOf(input, length);
            int m = length;
            while (m > 0) {
                int i = (int) Math.floor(Math.random() * m--);
                T tmp = arr[i];
                arr[i] = arr[m];
                arr[m] = tmp;
            }
            return arr;
        }

        /**
         * 从给定数组中随机获取 n 个元素
         * 做法是现将原数组打乱，然后抽取出 n 个元素
         */
        public static <T> T[] sampleSize(T[] input, int n) {
            T[] arr = shuffle(input);
            return Arrays.copyOfRange(arr, 0, Math.min(n, input.length));
        }

        /**
         * 交集
         * @see ThirtySecondsOfJava8Util.Array#intersection(int[], int[])
         */
        @SuppressWarnings("unchecked")
        public static <T> T[] similarity(T[] first, T[] second) {
            return Arrays.stream(first)
                    .filter(a -> Arrays.asList(second).contains(a))
                    .toArray(i -> (T[]) Arrays.copyOf(new Object[0], i, first.getClass()));
        }

        /**
         * 在保证顺序的顺序的前提下，返回将 el 插入到原数组中的最小索引
         */
        public static <T extends Comparable<? super T>> int sortedIndex(T[] arr, T el) {
            boolean isDescending = ArrayUtils.getLength(arr) < 2 || arr[0].compareTo(arr[arr.length - 1]) > 0;
            return IntStream.range(0, arr.length)
                    .filter(i -> isDescending ? el.compareTo(arr[i]) >= 0 : el.compareTo(arr[i]) <= 0)
                    .findFirst()
                    .orElse(arr.length);
        }

        /**
         * 对称差集。两数组中彼此不存在的元素。
         */
        @SuppressWarnings("unchecked")
        public static <T> T[] symmetricDifference(T[] first, T[] second) {
            Set<T> setA = new HashSet<>(Arrays.asList(first));
            Set<T> setB = new HashSet<>(Arrays.asList(second));

            return Stream.concat(
                    Arrays.stream(first).filter(a -> !setB.contains(a)),
                    Arrays.stream(second).filter(b -> !setA.contains(b))
            ).toArray(i -> (T[]) Arrays.copyOf(new Object[0], i, first.getClass()));
        }

        /**
         * 移除第一个元素后剩下的元素组成的数组
         */
        public static <T> T[] tail(T[] arr) {
            return arr.length > 1 ? Arrays.copyOfRange(arr, 1, arr.length) : arr;
        }

        /**
         * 获取前 n 个元素
         */
        public static <T> T[] take(T[] arr, int n) {
            return Arrays.copyOfRange(arr, 0, n);
        }

        /**
         * 获取末尾 n 个元素
         */
        public static <T> T[] takeRight(T[] arr, int n) {
            return Arrays.copyOfRange(arr, arr.length - n, arr.length);
        }

        /**
         * 并集
         */
        @SuppressWarnings("unchecked")
        public static <T> T[] union(T[] first, T[] second) {
            Set<T> set = new HashSet<>(Arrays.asList(first));
            set.addAll(Arrays.asList(second));
            return set.toArray((T[]) Arrays.copyOf(new Object[0], 0, first.getClass()));
        }


        @SuppressWarnings("unchecked")
        public static <T> T[] without(T[] arr, T... elements) {
            Set<T> excludeElements = new HashSet<>(Arrays.asList(elements));
            return Arrays.stream(arr)
                    .filter(el -> !excludeElements.contains(el))
                    .toArray(i -> (T[]) Arrays.copyOf(new Object[0], i, arr.getClass()));
        }

        /**
         * 将传入数组进行打包，返回一个数组列表。
         */
        public static List<Object[]> zip(Object[]... arrays) {
            // 获取传入数组的最大长度
            int maxLength = Arrays.stream(arrays).mapToInt(i -> i.length).max().orElse(0);
            return IntStream.range(0, maxLength)
                    .mapToObj(i -> Arrays.stream(arrays)
                            .map(arr -> i < arr.length ? arr[i] : null)
                            .toArray())
                    .collect(Collectors.toList());
        }

        /**
         * 以 props 的 length 为基准进行打包
         */
        public static Map<String, Object> zipObject(String[] props, Object[] values) {
            return IntStream.range(0, props.length)
                    // 不能使用 Map.entry()，它要求 key、value 非 null
                    .mapToObj(i -> new AbstractMap.SimpleEntry<>(props[i], i < values.length ? values[i] : null))
                    .collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), HashMap::putAll);
        }
    }


}
