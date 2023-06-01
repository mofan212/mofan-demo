package indi.mofan.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

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
        Object firstObj = objects.get(0);
        if (firstObj == null) {
            return;
        }
        Class<?> targetClazz = firstObj.getClass();
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

        for (Object object : objects) {
            Object target = ReflectionUtils.getField(field, object);
            List<?> list;
            if (StringUtils.endsWith(singlePath, DTO_LIST_SUFFIX)) {
                list = (List<?>) target;
            } else {
                list = Collections.singletonList(target);
            }
            doWith(list, pathArray, startIndex + 1, fieldCallback);
        }
    }

    public interface FieldCallback {
        void doWith(Field field, Object object) throws IllegalArgumentException, IllegalAccessException;
    }



    public static Deque<String> getPath(Object aggregateRoot, Object targetObject) {
        if (aggregateRoot instanceof List || targetObject instanceof List) {
            throw new RuntimeException("聚合根类型和目标类型不能是 List");
        }
        Deque<String> deque = new ArrayDeque<>();
        if (!getPathByBackTracking(aggregateRoot, targetObject, deque)) {
            throw new RuntimeException("未找到合适的路径");
        }
        return deque;
    }

    private static boolean getPathByBackTracking(Object sourceObject, Object targetObject, Deque<String> deque) {
        if (sourceObject instanceof List) {
            List<?> list = (List<?>) sourceObject;
            for (int i = 0; i < list.size(); i++) {
                deque.addLast(String.valueOf(i));
                if (getPathByBackTracking(list.get(i), targetObject, deque)) {
                    return true;
                }
                deque.removeLast();
            }
        } else {
            if (Objects.equals(sourceObject, targetObject)) {
                return true;
            }
            AtomicBoolean flag = new AtomicBoolean(false);
            ReflectionUtils.doWithFields(sourceObject.getClass(), field -> {
                deque.addLast(field.getName());
                ReflectionUtils.makeAccessible(field);
                Object object = ReflectionUtils.getField(field, sourceObject);
                if (field.getType().isAssignableFrom(List.class)) {
                    if (getPathByBackTracking(object, targetObject, deque)) {
                        flag.set(true);
                        return;
                    }
                    deque.removeLast();
                } else {
                    if (object == targetObject) {
                        flag.set(true);
                        return;
                    }
                    deque.removeLast();
                }
            });
            return flag.get();
        }
        return false;
    }

}
