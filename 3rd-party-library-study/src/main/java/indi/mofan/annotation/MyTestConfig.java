package indi.mofan.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mofan
 * @date 2023/3/27 18:06
 */
@MyContextConfiguration
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface MyTestConfig {
    @AliasFor(annotation = MyContextConfiguration.class, attribute = "locations")
    String[] value() default {};

    @AliasFor(annotation = MyContextConfiguration.class, attribute = "locations")
    String[] groovyScripts() default {};

    @AliasFor(annotation = MyContextConfiguration.class, attribute = "locations")
    String[] xmlFiles() default {};
}
