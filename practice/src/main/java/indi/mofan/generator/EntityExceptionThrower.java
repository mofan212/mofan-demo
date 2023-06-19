package indi.mofan.generator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author mofan
 * @date 2023/6/19 17:24
 */
public class EntityExceptionThrower<T extends EntityExceptionThrower.Entity> {
    /**
     * <p>
     * 聚合根内部实体细节：
     *     <ul>
     *         <li>一对多实体下可以有一对一子实体，也可以有一对多子实体</li>
     *         <li>一对一子实体下不能再有任何子实体</li>
     *     </ul>
     * </p>
     */
    private final List<T> aggregateRoots;

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

    private EntityExceptionThrower(List<T> aggregateRoots) {
        this.aggregateRoots = aggregateRoots;
    }

    @SneakyThrows
    public static <T extends Entity> List<Map<Integer, Object>> throwing(List<T> aggregateRoots) {
        return new EntityExceptionThrower<>(aggregateRoots).generateErrorStructures();
    }

    private List<Map<Integer, Object>> generateErrorStructures() {
        if (CollectionUtils.isEmpty(this.aggregateRoots)) {
            return Collections.emptyList();
        }
        List<Map<Integer, Object>> list = new ArrayList<>();
        for (T root : this.aggregateRoots) {
            Map<String, Object> map = new HashMap<>();
            // 利用回溯生成
            buildSubTreeByBackTracking(root, map);
            if (!map.isEmpty()) {
                list.add(Collections.singletonMap(root.getPositionIndex(), map));
            }
        }
        return list;
    }

    private void buildSubTreeByBackTracking(T aggregateRoot, Map<String, Object> map) {
        matchErrorObject(aggregateRoot, map);
        // 逐个处理子实体，非实体类型
        ReflectionUtils.doWithFields(aggregateRoot.getClass(), field -> {
            Class<?> fieldType = field.getType();
            // 排除简单类型
            if (SIMPLE_FIELD_TYPE.contains(fieldType)) {
                return;
            }
            ReflectionUtils.makeAccessible(field);
            // 一对多的子实体
            if (fieldType.isAssignableFrom(List.class)) {
                ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                if (!Entity.class.isAssignableFrom((Class<?>) genericType.getActualTypeArguments()[0])) {
                    return;
                }
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) ReflectionUtils.getField(field, aggregateRoot);
                if (CollectionUtils.isEmpty(list)) {
                    return;
                }
                List<Map<Integer, Object>> listStructure = new ArrayList<>(list.size());
                for (T subEntity : list) {
                    Map<String, Object> subStructure = new HashMap<>();
                    // 处理一对多下的子实体
                    buildSubTreeByBackTracking(subEntity, subStructure);
                    if (MapUtils.isNotEmpty(subStructure)) {
                        // single Map 的 key 不应该是列表的索引，而应该是对象中的索引字段
                        listStructure.add(Collections.singletonMap(subEntity.getPositionIndex(), subStructure));
                    }
                }
                if (CollectionUtils.isNotEmpty(listStructure)) {
                    map.put(field.getName(), listStructure);
                }
            } else { // 一对一子实体
                Object one2oneEntity = ReflectionUtils.getField(field, aggregateRoot);
                if (!(one2oneEntity instanceof Entity)) {
                    return;
                }
                Map<String, Object> subStructure = new HashMap<>();
                matchErrorObject((Entity) one2oneEntity, subStructure);
                if (MapUtils.isNotEmpty(subStructure)) {
                    map.put(field.getName(), subStructure);
                }
                // 一对一下面不会再有子实体，所以无需递归处理
            }
        });
    }

    private void matchErrorObject(Entity singleObject, Map<String, Object> structure) {
        if (CollectionUtils.isEmpty(singleObject.getErrorInfoList())) {
            return;
        }
        singleObject.getErrorInfoList().forEach(error -> structure.put(error.getErrorFieldName(), error.getErrorMessage()));
    }

    @Getter
    public static class Entity {
        @JsonIgnore
        private final List<ErrorObject> errorInfoList = new ArrayList<>();

        @Setter
        private Entity one2OneEntity;

        @Setter
        private List<Entity> entityList;

        @Setter
        private int positionIndex = -1;

        public void addErrorInfo(String errorFieldName, String errorMessage) {
            this.errorInfoList.add(ErrorObject.from(errorFieldName, errorMessage));
        }
    }

    @AllArgsConstructor(staticName = "from")
    public static class ErrorObject {

        private final String errorFieldName;
        private final String errorMessage;

        public String getErrorFieldName() {
            return errorFieldName;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
