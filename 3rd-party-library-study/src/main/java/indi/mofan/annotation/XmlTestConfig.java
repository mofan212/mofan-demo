package indi.mofan.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mofan
 * @date 2023/3/27 13:40
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@MyContextConfiguration
public @interface XmlTestConfig {
    @AliasFor(annotation = MyContextConfiguration.class, attribute = "locations")
    String[] xmlFiles();
}
