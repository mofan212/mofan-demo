package indi.mofan.collection;


import lombok.AllArgsConstructor;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Objects;

/**
 * @author mofan
 * @date 2025/3/1 14:13
 */
public class HashSetTest implements WithAssertions {

    @Test
    public void testOnlyOverrideEquals() {
        OnlyOverrideEqualsClass o1 = new OnlyOverrideEqualsClass("mofan", 23);
        OnlyOverrideEqualsClass o2 = new OnlyOverrideEqualsClass("mofan", 23);
        // 相等，但是拥有不同的哈希值
        assertThat(o1).isEqualTo(o2).doesNotHaveSameHashCodeAs(o2);

        var set = new HashSet<OnlyOverrideEqualsClass>();
        set.add(o1);
        set.add(o2);
        assertThat(set).hasSize(2);
    }

    @Test
    public void testOnlyOverrideHashCode() {
        OnlyOverrideHashCodeClass o1 = new OnlyOverrideHashCodeClass("mofan", 23);
        OnlyOverrideHashCodeClass o2 = new OnlyOverrideHashCodeClass("mofan", 23);
        // 不相等，但是拥有相同的哈希值
        assertThat(o1).isNotEqualTo(o2).hasSameHashCodeAs(o2);

        var set = new HashSet<OnlyOverrideHashCodeClass>();
        set.add(o1);
        set.add(o2);
        assertThat(set).hasSize(2);
    }


    @AllArgsConstructor
    static class OnlyOverrideEqualsClass {
        private String value;
        private Integer integer;

        @Override
        public boolean equals(Object o) {
            return o instanceof OnlyOverrideEqualsClass obj
                   && Objects.equals(value, obj.value)
                   && Objects.equals(integer, obj.integer);
        }
    }

    @AllArgsConstructor
    static class OnlyOverrideHashCodeClass {
        private String value;
        private Integer integer;

        @Override
        public int hashCode() {
            return Objects.hash(value, integer);
        }
    }
}
