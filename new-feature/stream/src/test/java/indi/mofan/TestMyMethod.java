package indi.mofan;

import org.junit.jupiter.api.Test;

/**
 * @author mofan
 * @date 2021/7/23 15:35
 */
public class TestMyMethod {
    @Test
    public void testGetPythagoreanTripleStream() {
        MyMethod.getPythagoreanTripleIntStream(1, 100, 5)
                .forEach(t -> System.out.println(t[0] + "," + t[1] + "," + t[2]));
        System.out.println();
        MyMethod.getPythagoreanTripleDoubleStream(1, 100, 5)
                .forEach(t -> System.out.println(t[0] + "," + t[1] + "," + t[2]));
    }
}
