package indi.mofan;


import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

/**
 * @author mofan
 * @date 2024/11/13 10:33
 */
public class StringUtilsTest implements WithAssertions {
    @Test
    public void testSplitWithMax() {
        String str = "1.2.3.4.5.6";
        String[] split = StringUtils.split(str, ".", 2);
        assertThat(split).hasSize(2)
                .containsExactly("1", "2.3.4.5.6");
    }
}
