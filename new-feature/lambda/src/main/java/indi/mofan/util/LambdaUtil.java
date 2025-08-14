package indi.mofan.util;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @author mofan
 * @date 2022/6/9 10:50
 */
public class LambdaUtil {

    public static String getImplClass(Serializable serializable) throws Exception {
        return getSerializedLambda(serializable)
                .map(i -> i.getImplClass().replace("/", "."))
                .orElse("");
    }

    public static String getImplMethodName(Serializable serializable) throws Exception {
        return getSerializedLambda(serializable)
                .map(SerializedLambda::getImplMethodName)
                .orElse("");
    }


    public static Optional<SerializedLambda> getSerializedLambda(Serializable serializable) throws Exception {
        Class<? extends Serializable> aClass = serializable.getClass();
        if (aClass.isSynthetic()) {
            Method method = aClass.getDeclaredMethod("writeReplace");
            method.setAccessible(true);
            return Optional.of((SerializedLambda) method.invoke(serializable));
        }
        return Optional.empty();
    }
}
