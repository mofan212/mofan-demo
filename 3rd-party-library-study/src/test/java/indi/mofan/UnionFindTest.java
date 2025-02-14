package indi.mofan;


import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * 自定义并查集与使用
 *
 * @author mofan
 * @date 2025/1/2 14:26
 */
public class UnionFindTest {
    @Test
    public void test() {
        UnionFind<Component> unionFind = new UnionFind<>(new HashSet<>());
        Component a = Component.of("A", 2L);
        unionFind.union(Component.of("B", 1L), a);
        Component c1 = Component.of("C", 3L);
        unionFind.union(a, c1);
        Component c2 = Component.of("C", 4L);
        unionFind.union(c1, c2);
        Component c3 = Component.of("C", 5L);
        unionFind.union(c2, c3);

        Component c4 = Component.of("C", 7L);
        unionFind.union(Component.of("C", 6L), c4);

        Component common = Component.of("D", 8L);

        unionFind.union(c3, common);
        unionFind.union(c4, common.copy());

        System.out.println(unionFind);
    }

    @ToString(exclude = "id")
    @RequiredArgsConstructor(staticName = "of")
    private static class Component {
        private final String name;
        private final Long dataId;
        private String id = "";

        public Component copy() {
            Component component = of(name, dataId);
            component.id = UUID.randomUUID().toString();
            return component;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, dataId, id);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Component component && Objects.equals(name, component.name) && Objects.equals(dataId, component.dataId) && Objects.equals(id, component.id);
        }
    }

    private static class UnionFind<T> extends org.jgrapht.alg.util.UnionFind<T> {

        /**
         * Creates a UnionFind instance with all the elements in separate sets.
         *
         * @param elements the initial elements to include (each element in a singleton set).
         */
        public UnionFind(Set<T> elements) {
            super(elements);
        }

        @Override
        public void addElement(T element) {
            if (!getParentMap().containsKey(element)) {
                super.addElement(element);
            }
        }

        @Override
        public void union(T element1, T element2) {
            if (!getParentMap().containsKey(element1)) {
                super.addElement(element1);
            }
            if (!getParentMap().containsKey(element2)) {
                super.addElement(element2);
            }
            super.union(element1, element2);
        }
    }
}
