package indi.mofan;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * @author mofan
 * @date 2024/7/13 14:18
 */
public class ParallelStreamTest implements WithAssertions {
    @Test
    public void testCreateCollector() {
        ArrayList<Object> arrayList = Stream.of(1, 2, 3, 4)
                .parallel()
                .collect(Collector.of(
                        ArrayList::new, // 如何创建容器
                        ArrayList::add, // 如何向容器添加数据
                        (list1, list2) -> {
                            list1.addAll(list2);
                            return list1;
                        },                          // 如何合并两个容器
                        list -> list                // 收尾
                        // 还有个特性参数：是否支持并发、是否需要收尾、是否要保证收集的顺序 -> 当前情况：不支持并发、需要收尾、要保证收集的顺序
                ));
        assertThat(arrayList).containsExactly(1, 2, 3, 4);
    }
}
