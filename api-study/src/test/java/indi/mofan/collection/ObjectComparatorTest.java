package indi.mofan.collection;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Lombok;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author mofan
 * @date 2025/11/22 16:31
 */
public class ObjectComparatorTest implements WithAssertions {

    @Test
    public void test() {
        List<Object> list = List.of("a", "b", "c");
        List<Object> sortedList = list.stream()
                .sorted(sortComparator(false, List.of(new Sort("DESC", null))))
                .toList();
        assertThat(sortedList).containsExactly("c", "b", "a");

        list = new ArrayList<>(List.of(
                new MyDto("a", 1),
                new MyDto("b", 2),
                new MyDto("a", 2)
        ));
        sortedList = list.stream()
                .sorted(sortComparator(true, List.of(new Sort("ASC", MyDto.Fields.str), new Sort("DESC", MyDto.Fields.num))))
                .toList();
        assertThat(sortedList).extracting(MyDto.Fields.str, MyDto.Fields.num)
                .containsExactly(tuple("a", 2), tuple("a", 1), tuple("b", 2));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldNameConstants
    static class MyDto {
        private String str;
        private Integer num;
    }

    record Sort(String sortType, String sortField) {
    }

    private Comparator<Object> sortComparator(boolean isDto, List<Sort> sortParams) {
        if (isDto) {
            if (CollectionUtils.isEmpty(sortParams)) {
                return Comparator.comparing(Objects::hashCode);
            }
            Comparator<Object> comparator = cast(comparator(sortParams.getFirst()));
            for (int i = 1; i < sortParams.size(); i++) {
                comparator = comparator.thenComparing(comparator(sortParams.get(i)));
            }
            return comparator;
        }
        return cast(nullsComparator(sortParams.getFirst().sortType()));
    }

    @SuppressWarnings("unchecked")
    static <T> T cast(Object object) {
        return (T) object;
    }

    private Comparator<Comparable<Object>> nullsComparator(String sortType) {
        return Objects.equals(sortType, "DESC")
                ? Comparator.nullsLast(Comparator.reverseOrder())
                : Comparator.nullsFirst(Comparator.naturalOrder());
    }

    @SuppressWarnings("unchecked")
    private Comparator<Object> comparator(Sort sortParam) {
        return Comparator.<Object, Comparable<?>>comparing(i -> (Comparable<Object>) getAttributeValue(
                        i,
                        sortParam.sortField()
                ),
                cast(nullsComparator(sortParam.sortType()))
        );
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object getAttributeValue(Object parentValue, String attributeName) {
        if (parentValue instanceof Map) {
            return MapUtils.getObject((Map) parentValue, attributeName);
        }
        // 不是 Map，那可能是 DTO，通过反射取
        Field field = ReflectionUtils.findField(parentValue.getClass(), attributeName);
        if (field == null) {
            throw new RuntimeException();
        }
        ReflectionUtils.makeAccessible(field);
        try {
            return FieldUtils.readField(field, parentValue);
        } catch (IllegalAccessException e) {
            throw Lombok.sneakyThrow(e);
        }
    }
}
