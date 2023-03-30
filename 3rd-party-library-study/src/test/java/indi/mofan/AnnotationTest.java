package indi.mofan;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author mofan
 * @date 2023/3/30 11:06
 */
public class AnnotationTest implements WithAssertions {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface InheritedA {
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface InheritedB {
    }

    @InheritedA
    static abstract class AbstractClass1 {
    }

    @InheritedB
    interface Interface1 {
    }

    static class Class1 extends AbstractClass1 implements Interface1 {
    }

    @Test
    public void testInherited() {
        Annotation[] annotations = Class1.class.getAnnotations();
        assertThat(annotations).isNotEmpty()
                .hasSize(1)
                .hasOnlyElementsOfTypes(InheritedA.class);
    }

    @Repeatable(Repeats.class)
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Repeat {
        String value();
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Repeats {
        Repeat[] value();
    }

    @Repeat("mofan")
    @Repeat("默烦")
    static class Class2 {
    }

    @Repeats({
            @Repeat("mofan"),
            @Repeat("默烦")
    })
    static class Class3 {
    }

    @Test
    public void testRepeatable() {
        String[] values = {"mofan", "默烦"};
        Annotation[] annotations = Class2.class.getAnnotations();
        // 两个 @Repeat 注解会合并成一个 @Repeats
        assertThat(annotations).hasSize(1)
                .hasOnlyElementsOfType(Repeats.class)
                .extracting(i -> ((Repeats) i).value())
                .flatMap(i -> Arrays.stream(i).collect(Collectors.toList()))
                .extracting(Repeat::value)
                .contains(values);

        annotations = Class3.class.getAnnotations();
        assertThat(annotations).hasSize(1)
                .hasOnlyElementsOfType(Repeats.class)
                .extracting(i -> ((Repeats) i).value())
                .flatMap(i -> Arrays.stream(i).collect(Collectors.toList()))
                .extracting(Repeat::value)
                .contains(values);
    }
}
