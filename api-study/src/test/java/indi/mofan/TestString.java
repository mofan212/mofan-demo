package indi.mofan;

import org.junit.Assert;
import org.junit.Test;

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
    }
}
