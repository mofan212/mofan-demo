package indi.mofan;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
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
}
