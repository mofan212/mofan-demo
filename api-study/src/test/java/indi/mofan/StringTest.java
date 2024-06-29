package indi.mofan;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

/**
 * @author mofan
 * @date 2024/6/29 14:46
 */
@SuppressWarnings("all")
public class StringTest implements WithAssertions {
    @Test
    public void testIntern() {
        String str1 = new String("string");
        String str2 = new String("string");
        assertThat(str1 == str2).isFalse();

        // 调用 intern
        String internStr1 = str1.intern();
        String internStr2 = str2.intern();
        assertThat(internStr1 == internStr2).isTrue();

        // 与原先的字符串比较
        assertThat(internStr1 == str1).isFalse();
        assertThat(internStr2 == str2).isFalse();
    }
}
