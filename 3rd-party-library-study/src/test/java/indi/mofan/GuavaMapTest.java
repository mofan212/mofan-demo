package indi.mofan;

import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.collect.TreeRangeMap;
import com.google.common.collect.TreeRangeSet;
import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mofan
 * @date 2023/9/27 16:03
 */
public class GuavaMapTest implements WithAssertions {

    private Map<String, Map<String, Integer>> buildNestedMap() {
        var nestedMap = new HashMap<String, Map<String, Integer>>();
        var firstMap = Map.of("1", 1, "2", 2);
        nestedMap.put("first", firstMap);
        var secondMap = Map.of("1", 100, "2", 200);
        nestedMap.put("second", secondMap);
        return nestedMap;
    }

    private Table<String, String, Integer> buildtTable() {
        Table<String, String, Integer> table = HashBasedTable.create();
        table.put("first", "1", 1);
        table.put("first", "2", 2);
        table.put("second", "1", 100);
        table.put("second", "2", 200);
        return table;
    }

    private <K, V> Map<K, Integer> reduceValue(Table<K, V, Integer> table) {
        return table.rowKeySet().stream()
                .collect(Collectors.toMap(Function.identity(), key -> table.row(key).values().stream().reduce(0, Integer::sum)));
    }

    @Test
    public void testHashBasedTable() {
        var nestedMap = buildNestedMap();
        assertThat(nestedMap.get("first").get("1")).isEqualTo(1);

        var table = buildtTable();
        assertThat(table.get("first", "1")).isEqualTo(1);

        // 获取行集合
        Set<String> rowKeySet = table.rowKeySet();
        assertThat(rowKeySet).contains("first", "second");
        // 获取列集合
        Set<String> columnKeySet = table.columnKeySet();
        assertThat(columnKeySet).contains("1", "2");
        // 获取值集合
        Collection<Integer> values = table.values();
        assertThat(values).contains(1, 2, 100, 200);

        // 求 key 对应的 value 的和
        Map<String, Integer> valueMap = reduceValue(table);
        assertThat(valueMap).containsExactly(entry("first", 3), entry("second", 300));

        // 行列的转置
        var transpose = Tables.transpose(table);
        assertThat(transpose.cellSet()).containsExactly(
                Tables.immutableCell("1", "first", 1),
                Tables.immutableCell("2", "first", 2),
                Tables.immutableCell("1", "second", 100),
                Tables.immutableCell("2", "second", 200)
        );

        // 转换为嵌套 map
        assertThat(table.rowMap()).isEqualTo(nestedMap);
        // 还可以使用 `table.columnMap()` 转，此时以 column 为 key
    }

    /**
     * 能够通过 key 和 value 相互获取
     */
    @Test
    public void testBiMap() {
        var map = HashBiMap.<String, String>create();
        map.put("1", "1");
        map.put("2", "2");
        // value 不能重复
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> map.put("3", "2"));
        assertThat(map).containsExactlyInAnyOrderEntriesOf(
                Map.of("1", "1", "2", "2")
        );

        // 也可以强制 put
        map.forcePut("3", "2");
        // 但是 key 会被替换掉
        assertThat(map).hasSize(2)
                .doesNotContain(entry("2", "2"))
                .contains(entry("3", "2"));

        assertThat(map.get("3")).isEqualTo("2");
        var inverse = map.inverse();
        assertThat(inverse.get("2")).isEqualTo("3");
        assertThat(inverse).containsExactlyInAnyOrderEntriesOf(
                Map.of("2", "3", "1", "1")
        );

        // inverse 后的并不是新的，而是原 map 的一个视图
        assertThat(map).hasSize(2);
        inverse.put("4", "4");
        // 3 -> 2
        assertThat(map).hasSize(3).extracting("3").isEqualTo("2");
        // 对 inverse 添加 2-2
        inverse.put("2", "2");
        // 2 -> 2
        assertThat(map).hasSize(3).extracting("2").isEqualTo("2");
    }

    @Test
    public void testMultiSet() {
        MultiSet<Integer> multiSet = new HashMultiSet<>();
        multiSet.add(1);
        multiSet.add(2, 2);
        // 所有元素的数量
        assertThat(multiSet.size()).isEqualTo(3);
        // 2 出现的次数
        assertThat(multiSet.getCount(2)).isEqualTo(2);
        // 将 3 出现的次数设置为 3
        multiSet.setCount(3, 3);
        assertThat(multiSet.size()).isEqualTo(6);

        Set<MultiSet.Entry<Integer>> entrySet = multiSet.entrySet();
        assertThat(entrySet).hasSize(3);
        entrySet.stream().filter(i -> i.getElement().equals(3)).findFirst()
                .map(MultiSet.Entry::getCount).ifPresent(i -> assertThat(i).isEqualTo(3));

        // 将 3 出现的次数减一
        multiSet.remove(3, 1);
        assertThat(multiSet.size()).isEqualTo(5);

        // 获取元素组成的集合
        Set<Integer> uniqueSet = multiSet.uniqueSet();
        assertThat(uniqueSet).containsAll(Set.of(1, 2, 3));
    }

    private Multimap<String, Integer> buildMultiMap() {
        var multimap = MultimapBuilder.hashKeys().arrayListValues().<String, Integer>build();
        multimap.put("1", 1);
        multimap.put("1", 2);
        multimap.put("1", 3);
        multimap.put("1", 4);
        multimap.put("2", 2);
        multimap.put("2", 4);
        return multimap;
    }

    @Test
    public void testMultimap() {
        Multimap<String, Integer> multiMap = buildMultiMap();
        assertThat(multiMap.get("1")).hasSize(4)
                .contains(1, 2, 3, 4);
        Collection<Integer> twoCollection = multiMap.get("2");
        assertThat(twoCollection).hasSize(2).contains(2, 4);
        // 返回的集合可以是 empty，但不会是 null
        assertThat(multiMap.get("3")).isNotNull().isEmpty();

        // 返回的集合是原 map 的视图，不是一个新的集合
        twoCollection.remove(2);
        twoCollection.add(0);
        assertThat(multiMap.get("2")).hasSize(2).containsExactly(4, 0);

        // 使用 asMap 转成 Map
        var map = multiMap.asMap();
        Collection<Integer> collection = map.get("1");
        assertThat(collection).hasSize(4).containsExactly(1, 2, 3, 4);
        collection.remove(1);
        assertThat(multiMap.get("1")).hasSize(3).containsExactly(2, 3, 4);

        // 数量问题
        Multimap<String, Integer> newMultiMap = buildMultiMap();
        assertThat(newMultiMap.size()).isEqualTo(6);
        assertThat(newMultiMap.entries()).hasSize(6);
        assertThat(newMultiMap.keySet()).hasSize(2);
        var newMap = newMultiMap.asMap();
        assertThat(newMap).hasSize(2);
    }

    @Test
    public void testRangeSet() {
        var rangeSet = TreeRangeSet.<Integer>create();
        // [1, 10]
        rangeSet.add(Range.closed(1, 10));
        // [1, 10], [11, 15)
        rangeSet.add(Range.closedOpen(11, 15));
        // 合并范围: [1, 10], [11, 20)
        rangeSet.add(Range.closedOpen(15, 20));
        // 忽略空范围: [1, 10], [11, 20)
        rangeSet.add(Range.openClosed(0, 0));
        // 删除空范围: [1, 5], [10, 10], [11, 20)
        rangeSet.remove(Range.open(5, 10));
        assertThat(rangeSet.asRanges()).containsExactlyInAnyOrder(
                Range.closed(1, 5),
                Range.closed(10, 10),
                Range.closedOpen(11, 20)
        );

        var anotherRangeSet = TreeRangeSet.<Integer>create();
        // 先调用下 Range#canonical()，能够更好地合并范围
        anotherRangeSet.add(Range.closed(1, 10).canonical(DiscreteDomain.integers()));
        anotherRangeSet.add(Range.closedOpen(11, 15));
        assertThat(anotherRangeSet.asRanges()).containsOnlyOnce(Range.closedOpen(1, 15));

        // 补集
        RangeSet<Integer> complement = rangeSet.complement();
        assertThat(complement.asRanges()).containsExactlyInAnyOrder(
                Range.lessThan(1),
                Range.open(5, 10),
                Range.open(10, 11),
                Range.atLeast(20)
        );

        // 交集
        assertThat(rangeSet.subRangeSet(Range.openClosed(-1, 2)).asRanges()).containsExactlyInAnyOrder(
                Range.closed(1, 2)
        );

        // 并集
        assertThat(rangeSet.span()).isEqualTo(Range.closedOpen(1, 20));

        // 是否包含
        assertThat(rangeSet.contains(0)).isFalse();
        assertThat(rangeSet.contains(2)).isTrue();

        // 包含并返回元素所在的 range，不包含时返回 null
        assertThat(rangeSet.rangeContaining(0)).isNull();
        assertThat(rangeSet.rangeContaining(2)).isEqualTo(Range.closed(1, 5));
        assertThat(anotherRangeSet.rangeContaining(2)).isEqualTo(Range.closedOpen(1, 15));

        // 判断给定的范围是否在 rangeSet 中
        assertThat(rangeSet.encloses(Range.closed(2, 3))).isTrue();
        assertThat(rangeSet.encloses(Range.closedOpen(19, 21))).isFalse();
    }

    @Test
    @SuppressWarnings("all")
    public void testRangeMap() {
        var rangeMap = TreeRangeMap.<Integer, String>create();
        // [0, 60)
        rangeMap.put(Range.closedOpen(0, 60), "不及格");
        // [60, 80)
        rangeMap.put(Range.closedOpen(60, 70), "及格");
        // [80, 90)
        rangeMap.put(Range.closedOpen(80, 90), "良好");
        // [90, 100]
        rangeMap.put(Range.closed(90, 100), "优秀");

        assertThat(rangeMap.get(0)).isEqualTo("不及格");
        assertThat(rangeMap.get(60)).isEqualTo("及格");
        assertThat(rangeMap.get(80)).isEqualTo("良好");
        assertThat(rangeMap.get(90)).isEqualTo("优秀");
        assertThat(rangeMap.get(100)).isEqualTo("优秀");

        // 还可以删除某段区间，删除后为 null，删除 [65, 85]
        rangeMap.remove(Range.closed(65, 85));
        assertThat(rangeMap.get(65)).isNull();
        assertThat(rangeMap.get(85)).isNull();
    }

    static class Person {
    }

    static class User {
    }

    @Test
    public void testClassToInstanceMap() {
        // 键是 Class，值是 Class 对应的实例对象
        var map = MutableClassToInstanceMap.create();
        Person person = new Person();
        map.put(Person.class, person);
        User user = new User();
        map.putInstance(User.class, user);

        assertThat(map.getInstance(Person.class)).isSameAs(person);
        assertThat(map.get(Person.class)).isSameAs(person);
        assertThat(map.getInstance(User.class)).isSameAs(user);
        assertThat(map.get(User.class)).isSameAs(user);
    }
}
