package indi.mofan.principle;

import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.BinaryOperator;
import java.util.function.Function;

/**
 * @author mofan
 * @date 2024/7/20 16:20
 */
public class LambdaPrincipleTest implements WithAssertions {
    @Test
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public void testLambdaMetaFactory() {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle impl = lookup.findStatic(
                LambdaPrincipleTest.class,
                "lambda$test$",
                MethodType.methodType(Integer.class, Integer.class, Integer.class)
        );
        CallSite callSite = LambdaMetafactory.metafactory(
                // 1. lookup
                lookup,
                // 2. 接口方法名
                "apply",
                // 3. 创建函数对象工厂方法的长相：方法的返回值、参数
                MethodType.methodType(BinaryOperator.class),
                // 4. 接口方法的长相，对于 BinaryOperator 来说，其方法使用了泛型，因此都是 Object
                MethodType.methodType(Object.class, Object.class, Object.class),
                // 5. 实现方法的 MethodHandle
                impl,
                // 6. 函数对象实际长相
                MethodType.methodType(Integer.class, Integer.class, Integer.class)
        );
        // 函数对象的工厂方法的 MethodHandle
        MethodHandle factory = callSite.getTarget();
        BinaryOperator<Integer> invoke = (BinaryOperator<Integer>) factory.invoke();
        // 上述大段代码等价于: BinaryOperator<Integer> lambda = (a, b) -> a + b;
        assertThat(invoke.apply(1, 2)).isEqualTo(3);
    }

    static final class MyLambda implements BinaryOperator<Integer> {
        private MyLambda() {
        }

        @Override
        public Integer apply(Integer a, Integer b) {
            return lambda$test$(a, b);
        }
    }

    private static Integer lambda$test$(Integer a, Integer b) {
        return a + b;
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public void testMethodReferenceMetaFactory() {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        // 需要成员方法 getName 的 MethodHandle
        MethodHandle impl = lookup.findVirtual(
                Student.class,
                "getName",
                MethodType.methodType(String.class)
        );
        CallSite cs = LambdaMetafactory.metafactory(
                lookup,
                "apply",
                MethodType.methodType(Function.class),
                MethodType.methodType(Object.class, Object.class),
                impl,
                MethodType.methodType(String.class, Student.class)
        );

        Function<Student, String> invoke = (Function<Student, String>) cs.getTarget().invoke();
        // Function<Student, String> func = Student::getName;
        Student student = new Student();
        student.name = "mofan";
        assertThat(invoke.apply(student)).isEqualTo("mofan");
    }

    static final class MyMethodReference implements Function<Student, String> {
        private MyMethodReference() {
        }

        @Override
        public String apply(Student student) {
            return student.getName();
        }
    }

    static class Student {
        private String name;

        public String getName() {
            return name;
        }
    }
}
