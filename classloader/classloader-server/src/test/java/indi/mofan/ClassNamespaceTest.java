package indi.mofan;

import indi.mofan.classloaders.MyCommonClassLoader;
import indi.mofan.util.TestUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Java 类的唯一性（命名空间）
 *
 * @author mofan
 * @date 2024/6/26 10:50
 */
public class ClassNamespaceTest implements WithAssertions {
    @Test
    public void test() throws Exception {
        MyCommonClassLoader l1 = new MyCommonClassLoader(TestUtils.COMMON_PATH);
        MyCommonClassLoader l2 = new MyCommonClassLoader(TestUtils.COMMON_PATH);

        String className = "indi.mofan.app.entities.Employee";
        Class<?> c1 = Class.forName(className, false, l1);
        Class<?> c2 = Class.forName(className, false, l2);
        Object o1 = c1.getDeclaredConstructor().newInstance();

        // 尽管 className 相同，但是 ClassLoader 不同，它们最终也不相同
        assertThat(c1).isNotEqualTo(c2);
        Assertions.assertNotEquals(c1, c2);
        /*
         * 检查 o1 是否是 c2 的实例。
         * 由于 c1、c2 不相同，因此 o1 不是 c2 的实例。
         */
        assertThat(c2.isInstance(o1)).isFalse();
    }
}
