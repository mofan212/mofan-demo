package indi.mofan.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mofan
 * @date 2023/3/27 18:16
 */
@MyTestConfig
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface GroovyOrXmlTestConfig {
    @AliasFor(annotation = MyTestConfig.class, attribute = "groovyScripts")
    String[] groovy() default {};

    @AliasFor(annotation = MyContextConfiguration.class, attribute = "locations")
    String[] xml() default {};
}
