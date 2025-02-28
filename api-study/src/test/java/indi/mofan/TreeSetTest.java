package indi.mofan;


import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.TreeSet;

/**
 * @author mofan
 * @date 2025/2/28 16:21
 */
public class TreeSetTest implements WithAssertions {

    @Test
    public void testTreeSetEqualsAndCompareTo() {
        // age 一样，name 不一样
        Person p1 = new Person("mofan", 23);
        Person p2 = new Person("MOFAN", 23);
        assertThat(p1).isNotEqualTo(p2);

        var set = new TreeSet<Person>();
        set.add(p1);
        set.add(p2);
        // 即使 equals 判定不等，但 compareTo 判断为相等，也不允许添加
        assertThat(set).containsOnly(p1);
    }

     record Person(String name, int age) implements Comparable<Person> {
        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }

        /**
         * name 和 age 都相同时，才认为相同
         */
        @Override
        public boolean equals(Object obj) {
            return obj instanceof Person(String _name, int _age)
                   && Objects.equals(this.name, _name)
                   && Objects.equals(this.age, _age);
        }

        /**
         * 只按 age 进行比较
         */
        @Override
        public int compareTo(Person o) {
            return this.age - o.age;
        }
    }
}
