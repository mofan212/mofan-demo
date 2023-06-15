package indi.mofan.generator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.util.ReflectionUtils;

import java.io.Serial;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author mofan
 * @date 2023/6/15 10:51
 */
public class ErrorStructureGenerator {
    /**
     * <p>
     * 聚合根内部实体细节：
     *     <ul>
     *         <li>一对多实体下可以有一对一子实体，也可以有一对多子实体</li>
     *         <li>一对一子实体下不能再有任何子实体</li>
     *     </ul>
     * </p>
     */
    private final Object aggregateRoot;
    private final List<ErrorObject<Object>> errorObjects = new LinkedList<>();
    private final Map<String, Object> errorStructure;

    /**
     * <p>
     * 聚合根中非对象类型的 Class 集合<br/>
     * <strong>注意：会存在 List 类型的 Enum</strong>
     * </p>
     */
    private static final Set<Class<?>> SIMPLE_FIELD_TYPE = Set.of(
            String.class, Integer.class, Long.class, Enum.class,
            BigDecimal.class, Boolean.class, Date.class
    );

    public static ErrorStructureGenerator from(Object aggregateRoot) {
        return new ErrorStructureGenerator(aggregateRoot, Collections.emptyList());
    }

    public static Map<String, Object> generate(Object aggregateRoot, Collection<ErrorObject<Object>> errorObjects) {
        ErrorStructureGenerator generator = new ErrorStructureGenerator(aggregateRoot, errorObjects);
        return generator.generate();
    }

    public void addErrorObject(Object object, String fieldName, String messageInfo) {
        this.errorObjects.add(ErrorObject.of(object, fieldName, messageInfo));
    }

    public void addErrorObjects(Collection<ErrorObject<Object>> errorObjects) {
        this.errorObjects.addAll(errorObjects);
    }

    private ErrorStructureGenerator(Object aggregateRoot, Collection<ErrorObject<Object>> errorObjects) {
        this.aggregateRoot = aggregateRoot;
        this.errorObjects.addAll(errorObjects);
        this.errorStructure = new HashMap<>();
    }

    public Map<String, Object> generate() {
        if (aggregateRoot instanceof Collection) {
            throw new RuntimeException("聚合根类型和目标类型不能是 List");
        }
        if (CollectionUtils.isEmpty(errorObjects)) {
            return Collections.emptyMap();
        }
        Map<String, Object> map = new HashMap<>();
        // 利用回溯生成
        buildSubTreeByBackTracking(this.aggregateRoot, map);
        this.errorStructure.putAll(map);
        return this.errorStructure;
    }

    private void buildSubTreeByBackTracking(Object object, Map<String, Object> map) {
        if (CollectionUtils.isEmpty(errorObjects)) {
            return;
        }
        matchErrorObject(map, object);
        // 逐个处理子实体，非实体类型
        ReflectionUtils.doWithFields(object.getClass(), field -> {
            Class<?> fieldType = field.getType();
            // 排除简单类型
            if (SIMPLE_FIELD_TYPE.contains(fieldType)) {
                return;
            }
            ReflectionUtils.makeAccessible(field);
            // 一对多的子实体
            if (fieldType.isAssignableFrom(List.class)) {
                ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                if (Enum.class.isAssignableFrom((Class<?>) genericType.getActualTypeArguments()[0])) {
                    return;
                }
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) ReflectionUtils.getField(field, object);
                if (CollectionUtils.isEmpty(list)) {
                    return;
                }
                List<Map<String, Object>> listStructure = new ArrayList<>(list.size());
                for (int i = 0; i < list.size(); i++) {
                    Map<String, Object> subStructure = new HashMap<>();
                    Object subEntity = list.get(i);
                    matchErrorObject(subStructure, subEntity);
                    // 处理一对多下的子实体
                    buildSubTreeByBackTracking(subEntity, subStructure);
                    if (MapUtils.isNotEmpty(subStructure)) {
                        // single Map 的 key 不应该是列表的索引，而应该是对象中的索引字段
                        listStructure.add(Collections.singletonMap(String.valueOf(i), subStructure));
                    }
                }
                if (CollectionUtils.isNotEmpty(listStructure)) {
                    map.put(field.getName(), listStructure);
                }
            } else { // 一对一子实体
                Map<String, Object> subStructure = new HashMap<>();
                matchErrorObject(subStructure, ReflectionUtils.getField(field, object));
                if (MapUtils.isNotEmpty(subStructure)) {
                    map.put(field.getName(), subStructure);
                }
                // 一对一下面不会再有子实体，所以无需递归处理
            }
        });
    }

    private void matchErrorObject(Map<String, Object> structure, Object singleObject) {
        Iterator<ErrorObject<Object>> iterator = errorObjects.iterator();
        while (iterator.hasNext()) {
            ErrorObject<Object> next = iterator.next();
            if (next.getLeft() == singleObject) {
                structure.put(next.getMiddle(), next.getRight());
                iterator.remove();
            }
        }
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ErrorObject<E> extends Triple<E, String, String> {
        @Serial
        private static final long serialVersionUID = 1930307767942706826L;

        private final ImmutableTriple<E, String, String> triple;

        public static <OBJ> ErrorObject<OBJ> of(OBJ object, String fieldName, String errorMessage) {
            return new ErrorObject<>(new ImmutableTriple<>(object, fieldName, errorMessage));
        }

        @Override
        public E getLeft() {
            return triple.getLeft();
        }

        @Override
        public String getMiddle() {
            return triple.getMiddle();
        }

        @Override
        public String getRight() {
            return triple.getRight();
        }
    }
}
