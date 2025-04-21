package indi.mofan.utils;


import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

/**
 * @author mofan
 * @date 2025/4/21 15:48
 */
public class ArrayUtilsTest implements WithAssertions {
    @Test
    public void testInsert() {
        String[] elements = new String[]{"a", "c", "d"};
        String[] newArray = ArrayUtils.insert(1, elements, "b");
        Assertions.assertThat(newArray)
                .containsExactly("a", "b", "c", "d");
    }
}
