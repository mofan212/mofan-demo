package indi.mofan;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

/**
 * @author mofan
 * @date 2023/6/11 21:52
 */
public class AnonymousInnerClassTest implements WithAssertions {
    public abstract static class TestClass<T> {
        public T t;

        public TestClass(T t) {
            this.t = t;
        }

        public abstract T func();
    }

    @Test
    public void test() {
        // Java 8 之前，匿名内部类无法使用菱形运算符完成类型推断
        TestClass<String> testClass = new TestClass<>("hello") {

            @Override
            public String func() {
                return "java";
            }
        };

        assertThat(testClass.func()).isEqualTo("java");
    }
}
