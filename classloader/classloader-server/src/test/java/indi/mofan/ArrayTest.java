package indi.mofan;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

/**
 * 数组类的本质
 *
 * @author mofan
 * @date 2024/6/26 10:45
 */
public class ArrayTest implements WithAssertions {
    @Test
    public void test() {
        int[] ia = new int[3];
        assertThat(ia.getClass().toString())
                .isEqualTo("class [I");
        // 数组类的父类
        assertThat(ia.getClass().getSuperclass())
                .isEqualTo(Object.class);
        // 数组类的父接口
        Class<?>[] interfaces = ia.getClass().getInterfaces();
        assertThat(interfaces).containsExactly(Cloneable.class, Serializable.class);
    }
}
