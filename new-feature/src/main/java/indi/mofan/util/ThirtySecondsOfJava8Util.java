package indi.mofan.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
         *
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

    public static class Maths {
        /**
         * 平均值
         */
        public static OptionalDouble average(int[] arr) {
            return IntStream.of(arr).average();
        }

        /**
         * 最大公因数
         */
        public static OptionalInt gcd(int[] numbers) {
            return Arrays.stream(numbers)
                    .reduce(Maths::gcd);
        }

        private static int gcd(int a, int b) {
            if (b == 0) {
                return a;
            }
            return gcd(b, a % b);
        }

        /**
         * 最大公倍数
         */
        public static OptionalInt lcm(int[] numbers) {
            IntBinaryOperator lcm = (x, y) -> (x * y) / gcd(x, y);
            return Arrays.stream(numbers).reduce(lcm);
        }

        /**
         * 返回大于或等于给定值的下一个幂
         */
        public static int findNextPositivePowerOfTwo(int value) {
            /*
             * Integer#numberOfLeadingZeros() 方法的作用：
             * 返回给定 int 值用二进制补码形式表示的最高非 0 位前面的 0 的个数
             */
            return 1 << (32 - Integer.numberOfLeadingZeros(value - 1));
        }

        /**
         * 是否为偶数。
         * 假设给定数是 4，二进制表示为 100，二进制 1 表示为 001，100 & 001 == 0
         */
        public static boolean isEven(final int value) {
            // 0b 或 0B 表示二进制数，比如 0b1 表示用二进制数表示的 1
            return (value & 0b1) == 0;
        }

        /**
         * 判断一个数是否是二的幂
         */
        public static boolean isPowerOfTwo(final int value) {
            // 如果一个数是二的幂，那么 ~value + 1 等于她本身，比如 ~100 + 1 = 011 + 1 = 100
            return value > 0 && ((value & (~value + 1)) == value);
        }

        public static int generateRandomInt() {
            return ThreadLocalRandom.current().nextInt();
        }
    }

    public static class Strings {
        /**
         * 返回由给定字符串中所有字符全排列后组成的字符串
         */
        public static List<String> anagrams(String input) {
            if (input.length() <= 2) {
                return input.length() == 2
                        ? Arrays.asList(input, input.substring(1) + input.charAt(0))
                        : Collections.singletonList(input);
            }
            return IntStream.range(0, input.length())
                    .mapToObj(i -> Map.entry(i, input.substring(i, i + 1)))
                    .flatMap(entry ->
                            anagrams(input.substring(0, entry.getKey()) + input.substring(entry.getKey() + 1))
                                    .stream()
                                    .map(s -> entry.getValue() + s))
                    .collect(Collectors.toList());
        }

        public static int byteSize(String input) {
            return input.getBytes().length;
        }

        /**
         * 将给定字符串的首字母大写，lowerRest 可以决定后续字母是否全小写
         */
        public static String capitalize(String input, boolean lowerRest) {
            return input.substring(0, 1).toUpperCase() +
                   (lowerRest
                           ? input.substring(1).toLowerCase()
                           : input.substring(1));
        }

        private static final Pattern WORD_PATTERN = Pattern.compile("\\b(?=\\w)");

        public static String capitalizeEveryWord(final String input) {
            return WORD_PATTERN.splitAsStream(input)
                    .map(w -> capitalize(w, false))
                    .collect(Collectors.joining());
        }

        /**
         * 返回字符串中元音的字符的数量
         */
        public static int countVowels(String input) {
            return input.replaceAll("[^aeiouAEIOU]", "").length();
        }

        /**
         * 转义在正则中需要使用的字符串
         */
        public static String escapeRegExp(String input) {
            return Pattern.quote(input);
        }

        /**
         * 将驼峰转换成以指定分隔符拼接的字符串
         */
        public static String fromCamelCase(String input, String separator) {
            return input
                    .replaceAll("([a-z\\d])([A-Z])", "$1" + separator + "$2")
                    .toLowerCase();
        }

        private static final Pattern ABSOLUTE_URL_PATTERN = Pattern.compile("^[a-z][a-z0-9+.-]*:");

        /**
         * 给定字符串是否是绝对 url
         */
        public static boolean isAbsoluteUrl(String url) {
            return ABSOLUTE_URL_PATTERN.matcher(url).find();
        }

        /**
         * 给定字符串是否全小写
         */
        public static boolean isLowerCase(String input) {
            return Objects.equals(input, input.toLowerCase());
        }

        public static boolean isUpperCase(String input) {
            return Objects.equals(input, input.toUpperCase());
        }

        /**
         * 给定字符串是否是回文
         */
        public static boolean isPalindrome(String input) {
            String s = input.toLowerCase().replaceAll("[\\W_]", "");
            return Objects.equals(
                    s,
                    new StringBuilder(s).reverse().toString()
            );
        }

        public static boolean isNumeric(final String input) {
            return IntStream.range(0, input.length())
                    .allMatch(i -> Character.isDigit(input.charAt(i)));
        }

        /**
         * 用给定字符替换除最后 num 个字符以外的所有字符
         * num 为整数，表示保留后 num 个字符；num 为负数，表示保留前 num 个字符
         */
        public static String mask(String input, int num, String mask) {
            int length = input.length();
            return num > 0
                    ?
                    input.substring(0, length - num).replaceAll("(.)", mask)
                    + input.substring(length - num)
                    :
                    input.substring(0, Math.negateExact(num))
                    + input.substring(Math.negateExact(num), length).replaceAll("(.)", mask);
        }

        public static String reverse(String input) {
            return new StringBuilder(input).reverse().toString();
        }

        /**
         * 按字符顺序对字符串的字符进行排序
         */
        public static String sortCharactersInString(String input) {
            return Arrays.stream(input.split("")).sorted().collect(Collectors.joining());
        }

        /**
         * 多行字符串拆分为数组
         */
        public static String[] splitLines(String input) {
            return input.split("\\r?\\n");
        }

        private static final Pattern PATTERN = Pattern.compile("[A-Z]{2,}(?=[A-Z][a-z]+[0-9]*|\\b)|[A-Z]?[a-z]+[0-9]*|[A-Z]|[0-9]+");

        public static String toCamelCase(String input) {
            Matcher matcher = PATTERN.matcher(input);
            List<String> matchedParts = new ArrayList<>();
            while (matcher.find()) {
                matchedParts.add(matcher.group(0));
            }
            String s = matchedParts.stream()
                    .map(x -> x.substring(0, 1).toUpperCase() + x.substring(1).toLowerCase())
                    .collect(Collectors.joining());
            return s.substring(0, 1).toLowerCase() + s.substring(1);
        }

        public static String toKebabCase(String input) {
            Matcher matcher = PATTERN.matcher(input);
            List<String> matchedParts = new ArrayList<>();
            while (matcher.find()) {
                matchedParts.add(matcher.group(0));
            }
            return matchedParts.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.joining("-"));
        }

        /**
         * 正则拆分
         */
        public static List<String> match(String input, String regex) {
            Matcher matcher = Pattern.compile(regex).matcher(input);
            List<String> matchedParts = new ArrayList<>();
            while (matcher.find()) {
                matchedParts.add(matcher.group(0));
            }
            return matchedParts;
        }

        public static String toSnakeCase(String input) {
            Matcher matcher = PATTERN.matcher(input);
            List<String> matchedParts = new ArrayList<>();
            while (matcher.find()) {
                matchedParts.add(matcher.group(0));
            }
            return matchedParts.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.joining("_"));
        }

        /**
         * 将字符串截断到指定的长度，最后三位始终以 `...` 代替
         */
        public static String truncate(String input, int num) {
            return input.length() > num
                    ? input.substring(0, num > 3 ? num - 3 : num) + "..."
                    : input;
        }

        /**
         * 给定字符串转换为单词数组
         */
        public static String[] words(String input) {
            return Arrays.stream(input.split("[^a-zA-Z-]+"))
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);
        }

        /**
         * 将由空格分隔的数字字符串转换为 int 数组
         */
        public static int[] stringToIntegers(String numbers) {
            return Arrays.stream(numbers.split(" ")).mapToInt(Integer::parseInt).toArray();
        }
    }

    public static class IO {
        /**
         * InputStream 转字符串
         */
        public static String convertInputStreamToString(final InputStream in) throws IOException {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString(StandardCharsets.UTF_8);
        }

        /**
         * 将文件内容写入字符串
         */
        public static String readFileAsString(Path path) throws IOException {
            return new String(Files.readAllBytes(path));
        }

        /**
         * 获取当前工作目录
         */
        public static String getCurrentWorkingDirectoryPath() {
            return FileSystems.getDefault().getPath("").toAbsolutePath().toString();
        }

        /**
         * 获取系统 java.io.tmpdir 的值，如果没以分隔符结尾，则加一个
         */
        public static String tmpDirName() {
            String tmpDirName = System.getProperty("java.io.tmpdir");
            if (!tmpDirName.endsWith(File.separator)) {
                tmpDirName += File.separator;
            }
            return tmpDirName;
        }
    }

    public static class Exceptions {
        /**
         * 异常堆栈转换为字符串
         */
        public static String stackTraceAsString(final Throwable throwable) {
            final StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));
            return sw.toString();
        }
    }
}
