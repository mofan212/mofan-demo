package indi.mofan;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author mofan
 * @date 2021/7/23 15:09
 */
public class MyMethod {

    /**
     * 给定两个数，获取这两个数之间能构成的勾股数的 limit 个数组
     *
     * @param start 开始值
     * @param end 结束值
     * @param limit 多少个勾股数组
     * @return 数组 Stream
     */
    public static Stream<int[]> getPythagoreanTripleIntStream(int start, int end, int limit) {
        Stream<int[]> intArrayStream = IntStream.rangeClosed(start, end).boxed()
                .flatMap(i ->
                        IntStream.rangeClosed(i, end)
                                .filter(j -> Math.sqrt(i * i + j * j) % 1 == 0)
                                .mapToObj(j -> new int[]{i, j, (int) Math.sqrt(i * i + j * j)})
                );
        return intArrayStream.limit(limit);
    }

    /**
     * 给定两个数，获取这两个数之间能构成的勾股数的 limit 个数组
     *
     * @param start 开始值
     * @param end 结束值
     * @param limit 多少个勾股数组
     * @return 数组 Stream
     */
    public static Stream<double[]> getPythagoreanTripleDoubleStream(int start, int end, int limit) {
        Stream<double[]> intArrayStream = IntStream.rangeClosed(start, end).boxed()
                .flatMap(i ->
                        IntStream.rangeClosed(i, end)
                                .mapToObj(j -> new double[]{i, j, Math.sqrt(i * i + j * j)})
                                .filter(t -> t[2] % 1 == 0)
                );
        return intArrayStream.limit(limit);
    }
}
