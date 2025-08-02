package indi.mofan;


import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

/**
 * @author mofan
 * @date 2025/7/30 19:37
 */
public class LogTrickTest implements WithAssertions {

    final static int[] ARR = {2, 4, 5, 7};

    private int[] getArray() {
        return ARR.clone();
    }

    @Test
    public void testOr() {
        int[] copy = getArray();
        for (int i = 0; i < copy.length; i++) {
            int x = copy[i];
            for (int j = i - 1; j >= 0; j--) {
                copy[j] = copy[j] | x;
            }
            switch (i) {
                case 0 -> assertThat(copy).containsExactly(2, 4, 5, 7);
                case 1 -> assertThat(copy).containsExactly(6, 4, 5, 7);
                case 2 -> assertThat(copy).containsExactly(7, 5, 5, 7);
                case 3 -> assertThat(copy).containsExactly(7, 7, 7, 7);
            }
        }
    }

    @Test
    public void testLogTrickOr() {
        int[] nums = new int[]{2, 4, 5, 7};
        for (int i = 0; i < nums.length; i++) {
            int x = nums[i];
            for (int j = i - 1; j >= 0; j--) {
                // 判断是否需要提前结束循环
                if ((nums[j] | x) == nums[j]) {
                    break;
                }
                nums[j] = nums[j] | x;
            }
            switch (i) {
                case 0 -> assertThat(nums).containsExactly(2, 4, 5, 7);
                case 1 -> assertThat(nums).containsExactly(6, 4, 5, 7);
                case 2 -> assertThat(nums).containsExactly(7, 5, 5, 7);
                case 3 -> assertThat(nums).containsExactly(7, 7, 7, 7);
            }
        }
    }
}
