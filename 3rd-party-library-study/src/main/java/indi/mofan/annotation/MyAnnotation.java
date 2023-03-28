package indi.mofan.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mofan
 * @date 2023/3/28 10:21
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@MyMetaAnnotation
public @interface MyAnnotation {
    @AliasFor(annotation = MyMetaAnnotation.class, attribute = "value")
    String myValue() default "testValue";

    @AliasFor(annotation = MyMetaAnnotation.class, attribute = "sort")
    int mySort() default 0;
}
