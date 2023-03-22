package indi.mofan.reflections.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mofan
 * @date 2021/3/22 9:51
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface AnnotationForMethod {
}
