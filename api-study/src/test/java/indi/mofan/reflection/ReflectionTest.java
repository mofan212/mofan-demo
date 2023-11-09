package indi.mofan.reflection;

import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * @author mofan
 * @date 2023/10/22 21:51
 */
public class ReflectionTest implements WithAssertions {
    static class MyClass {
        public void firstMethod(int... ints) {
            System.out.println("call firstMethod");
        }

        public void secondMethod(String... strings) {
            System.out.println("call secondMethod");
        }
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("all")
    public void testVaryArgsMethod() {
        MyClass obj = new MyClass();
        Class<MyClass> clazz = MyClass.class;

        Method firstMethod = clazz.getDeclaredMethod("firstMethod", int[].class);
        firstMethod.invoke(obj, new int[]{1, 2, 3});
        firstMethod.invoke(obj, (Object) new int[]{1, 2, 3});
        firstMethod.invoke(obj, new Object[]{new int[]{1, 2, 3}});

        Method secondMethod = clazz.getDeclaredMethod("secondMethod", String[].class);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> secondMethod.invoke(obj, new String[]{"a", "b", "c"}))
                .withMessageContaining("wrong number of arguments");

        // 强转为 Object
        secondMethod.invoke(obj, (Object) new String[]{"a", "b", "c"});
        // 或者是 Object[] 中的一个元素
        secondMethod.invoke(obj, new Object[]{new String[]{"a", "b", "c"}});
    }
}
