package indi.mofan.utils;


import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

/**
 * @author mofan
 * @date 2024/11/13 10:33
 */
public class StringUtilsTest implements WithAssertions {

    @Test
    public void testSplit() {
        String str = "/a/b/c/d/";
        assertThat(str.split("/")).hasSize(5)
                .containsExactly("", "a", "b", "c", "d");

        assertThat(StringUtils.split(str, "/")).hasSize(4)
                .containsExactly("a", "b", "c", "d");
    }

    @Test
    public void testSplitWithMax() {
        String str = "1.2.3.4.5.6";
        String[] split = StringUtils.split(str, ".", 2);
        assertThat(split).hasSize(2)
                .containsExactly("1", "2.3.4.5.6");

        str = "12ab34ab56ab78";
        split = StringUtils.splitByWholeSeparator(str, "ab", 1);
        assertThat(split).hasSize(1)
                .containsExactly("12ab34ab56ab78");
        split = StringUtils.splitByWholeSeparator(str, "ab", 2);
        assertThat(split).hasSize(2)
                .containsExactly("12", "34ab56ab78");
    }

    @Test
    public void testJoin() {
        String[] elements = new String[]{"a", "b", "c"};
        String join = StringUtils.join(elements);
        assertThat(join).isEqualTo("abc");
    }
}
