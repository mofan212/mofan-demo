package indi.mofan;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author mofan
 * @date 2022/11/2 14:34
 */
public class TestStringJoiner {

    @Test
    public void testSimpleToUse() {
        StringJoiner joiner = new StringJoiner(",");
        joiner.add("hello")
                .add("world");
        Assertions.assertEquals("hello,world", joiner.toString());
    }

    @Test
    public void testEmptyValue() {
        StringJoiner joiner = new StringJoiner("-");
        Assertions.assertEquals("", joiner.toString());
        joiner = new StringJoiner("-", "==>", "<==");
        // 没添加任何内容时，返回前缀和后缀的拼接
        Assertions.assertEquals("==><==", joiner.toString());
        // 指定空值
        joiner.setEmptyValue("empty");
        Assertions.assertEquals("empty", joiner.toString());
    }

    @Test
    public void testPrefixAndSuffix() {
        StringJoiner joiner = new StringJoiner("-", "==>", "<==");
        joiner.add("1").add("2");
        Assertions.assertEquals("==>1-2<==", joiner.toString());

        joiner = new StringJoiner("-", "", "<==");
        joiner.add("1").add("2");
        Assertions.assertEquals("1-2<==", joiner.toString());

        joiner = new StringJoiner("-", "==>", "");
        joiner.add("1").add("2");
        Assertions.assertEquals("==>1-2", joiner.toString());
    }

    @Test
    public void testLength() {
        StringJoiner joiner = new StringJoiner("-");
        Assertions.assertEquals(0, joiner.length());

        joiner = new StringJoiner("-", "==>", "<==");
        Assertions.assertEquals(6, joiner.length());

        String emptyValue = "empty";
        joiner.setEmptyValue(emptyValue);
        Assertions.assertEquals(emptyValue.length(), joiner.length());

        joiner.add("1").add("2");
        Assertions.assertEquals(9, joiner.length());
    }

    @Test
    public void testMerge() {
        StringJoiner joiner = new StringJoiner("-", "==>", "<==");
        try {
            joiner.merge(null);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof NullPointerException);
        }

        StringJoiner other = new StringJoiner(",", "==@", "@==");
        other.add("one").add("two");
        joiner.merge(other);
        Assertions.assertEquals("==>one,two<==", joiner.toString());

        // merge 过一次，重新 new 一个
        joiner = new StringJoiner("-", "==>", "<==");
        joiner.add("1").add("2");
        joiner.merge(other);
        Assertions.assertEquals("==>1-2-one,two<==", joiner.toString());
    }

    @Test
    public void testEmptyJoin() {
        String str1 = String.join("-", "", "java.lang.String");
        Assertions.assertEquals("-java.lang.String", str1);
        String str2 = String.join("-", "java.lang.Integer", "java.lang.String");
        Assertions.assertEquals("java.lang.Integer-java.lang.String", str2);

        StringJoiner joiner = new StringJoiner("-");
        joiner.add("").add("java.lang.String");
        Assertions.assertEquals("-java.lang.String", joiner.toString());

        List<String> strList = new ArrayList<>(Arrays.asList("1", "2", "3"));
        Assertions.assertEquals("1-2-3", String.join("-", strList));
        Assertions.assertTrue(StringUtils.isEmpty(String.join("-", new ArrayList<>())));
    }
}
