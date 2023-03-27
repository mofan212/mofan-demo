package indi.mofan.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mofan
 * @date 2023/3/27 11:01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface MyContextConfiguration {
    @AliasFor("locations")
    String[] value() default {};

    @AliasFor("value")
    String[] locations() default {};
}
