package indi.mofan.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * @author mofan
 * @date 2023/1/12 17:04
 */
public class ReflectionUtil {

    private static final String DTO_LIST_SUFFIX = "DtoList";

    public static void doWith(List<?> objects, String[] pathArray, FieldCallback fieldCallback) {
        doWith(objects, pathArray, 0, fieldCallback);
    }

    private static void doWith(List<?> objects, String[] pathArray, int startIndex, FieldCallback fieldCallback) {
        if (ArrayUtils.isEmpty(pathArray) || CollectionUtils.isEmpty(objects)) {
            return;
        }

        Object target = objects.get(0);
        Class<?> targetClazz = target.getClass();

        int maxIndex = pathArray.length - 1;
        String singlePath = pathArray[startIndex];
        Field field = ReflectionUtils.findField(targetClazz, singlePath);
        if (field == null) {
            return;
        }
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        if (startIndex == maxIndex) {
            objects.forEach(i -> {
                try {
                    fieldCallback.doWith(field, i);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
            return;
        }
        target = ReflectionUtils.getField(field, target);
        List<?> list;
        if (StringUtils.endsWith(singlePath, DTO_LIST_SUFFIX)) {
            list = (List<?>) target;
        } else {
            list = Collections.singletonList(target);
        }
        doWith(list, pathArray, startIndex + 1, fieldCallback);
    }

    public interface FieldCallback {
        void doWith(Field field, Object object) throws IllegalArgumentException, IllegalAccessException;
    }
}
