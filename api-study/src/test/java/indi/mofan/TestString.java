package indi.mofan;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author mofan
 * @date 2022/11/2 14:34
 */
public class TestString {
    @Test
    public void testEmptyJoin() {
        String str1 = String.join("-", "", "java.lang.String");
        Assert.assertEquals("-java.lang.String", str1);
        String str2 = String.join("-", "java.lang.Integer", "java.lang.String");
        Assert.assertEquals("java.lang.Integer-java.lang.String", str2);

        StringJoiner joiner = new StringJoiner("-");
        joiner.add("").add("java.lang.String");
        Assert.assertEquals("-java.lang.String", joiner.toString());

        List<String> strList = new ArrayList<>(Arrays.asList("1", "2", "3"));
        Assert.assertEquals("1-2-3", String.join("-", strList));
        Assert.assertTrue(StringUtils.isEmpty(String.join("-", new ArrayList<>())));
    }
}
