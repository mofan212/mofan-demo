package indi.mofan.util;

import indi.mofan.lambda.SFunction;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mofan
 * @date 2022/6/9 16:26
 */
public class ReflectionUtil {
    public static final String GETTER_PREFIX = "get";
    public static final String BOOLEAN_GETTER_PREFIX = "is";
    public static final String LAMBDA_PREFIX = "lambda$";
    private static final Map<SFunction<?, ?>, Field> CACHE = new ConcurrentHashMap<>();

    public static <T, R> String getFieldName(SFunction<T, R> function) {
        return getField(function).getName();
    }

    public static Field getField(SFunction<?, ?> function) {
        return CACHE.computeIfAbsent(function, ReflectionUtil::findField);
    }

    private static Field findField(SFunction<?, ?> function) {
        Field field;
        String fieldName;
        try {
            Optional<SerializedLambda> serializedLambda = LambdaUtil.getSerializedLambda(function);
            String implMethodName = serializedLambda.map(SerializedLambda::getImplMethodName).orElse("");
            if (implMethodName.startsWith(GETTER_PREFIX) && implMethodName.length() > GETTER_PREFIX.length()) {
                fieldName = Introspector.decapitalize(implMethodName.substring(GETTER_PREFIX.length()));
            } else if (implMethodName.startsWith(BOOLEAN_GETTER_PREFIX) && implMethodName.length() > BOOLEAN_GETTER_PREFIX.length()) {
                fieldName = Introspector.decapitalize(implMethodName.substring(BOOLEAN_GETTER_PREFIX.length()));
            } else if (implMethodName.startsWith(LAMBDA_PREFIX)) {
                throw new IllegalArgumentException("不支持 Lambda 表达式，请使用方法引用");
            } else {
                throw new IllegalArgumentException(implMethodName + "不是 Getter 方法引用");
            }
            String implClass = serializedLambda.map(SerializedLambda::getImplClass).orElse("").replace("/", ".");
            Class<?> aClass = Class.forName(implClass, false, ClassUtils.getDefaultClassLoader());

            // 通过 Spring 的 ReflectionUtils 获取 Field 对象
            field = ReflectionUtils.findField(aClass, fieldName);
            if (field != null) {
                return field;
            } else {
                throw new NoSuchFieldException(fieldName);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("字段信息获取失败 " + e.getMessage());
        }
    }
}
