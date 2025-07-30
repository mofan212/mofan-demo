package indi.mofan;


import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
            List<Integer> list = new ArrayList<>();
            for (int j = i - 1; j >= 0; j--) {
                copy[j] = copy[j] | x;
                list.add(copy[j]);
            }
            switch (i) {
                case 0 -> assertThat(list).isEmpty();
                case 1 -> assertThat(list).containsExactly(6);
                case 2 -> assertThat(list).containsExactly(5, 7);
                case 3 -> assertThat(list).containsExactly(7, 7, 7);
            }
        }
    }
}
