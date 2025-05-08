package indi.mofan.reflection;


import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author mofan
 * @date 2025/5/8 9:29
 */
public class MethodTest implements WithAssertions {

    @Test
    @SneakyThrows
    public void testGetMethodParameterDetails() {
        Optional<Method> methodOpt = Arrays.stream(MyClass.class.getDeclaredMethods())
                .filter(i -> i.getName().equals("func1"))
                .findFirst();
        assertThat(methodOpt).isPresent();

        Method method = methodOpt.get();

        Type[] genericParameterTypes = method.getGenericParameterTypes();
        assertThat(genericParameterTypes).hasSize(1);
        Type genericParameterType = genericParameterTypes[0];
        assertThat(genericParameterType).isInstanceOf(ParameterizedType.class);
        if (genericParameterType instanceof ParameterizedType paramType) {
            Type rawType = paramType.getRawType();
            assertThat(rawType).isEqualTo(Set.class);

            Type[] typeArgs = paramType.getActualTypeArguments();
            assertThat(typeArgs).hasSize(1)
                    .containsOnly(Integer.class);
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        assertThat(parameterTypes).hasSize(1);
        assertThat(parameterTypes[0]).isEqualTo(Set.class);


        Type genericReturnType = method.getGenericReturnType();
        assertThat(genericReturnType).isInstanceOf(ParameterizedType.class);
        if (genericReturnType instanceof ParameterizedType returnType) {
            Type rawType = returnType.getRawType();
            assertThat(rawType).isEqualTo(List.class);

            Type[] typeArgs = returnType.getActualTypeArguments();
            assertThat(typeArgs).hasSize(1)
                    .containsOnly(String.class);
        }

        method = MyClass.class.getMethod("func2", Integer.class);
        Class<?> returnType = (Class<?>) method.getGenericReturnType();
        assertThat(returnType).isEqualTo(Return.class);
        MyAnnotation annotation = returnType.getAnnotation(MyAnnotation.class);
        assertThat(annotation).extracting(MyAnnotation::value).isEqualTo("xyz");
    }

    static class MyClass {
        public List<String> func1(Set<Integer> integers) {
            return List.of();
        }

        public Return func2(Integer integer) {
            return null;
        }
    }

    @MyAnnotation("xyz")
    static class Return {
    }

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface MyAnnotation {
        String value();
    }
}
