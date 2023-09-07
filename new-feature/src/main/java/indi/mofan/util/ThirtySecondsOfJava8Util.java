package indi.mofan.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author mofan
 * @date 2023/9/7 20:21
 */
public class ThirtySecondsOfJava8Util {
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
}
