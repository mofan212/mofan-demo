package indi.mofan.higher;

import com.google.common.collect.Lists;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

/**
 * @author mofan
 * @date 2024/7/10 13:53
 */
public class InnerLoopTest implements WithAssertions {

    private <T> void higherOrder(List<T> list, Consumer<T> consumer) {
        // 指定迭代器中第一个元素的索引
        ListIterator<T> iterator = list.listIterator(list.size());
        // 倒着遍历
        while (iterator.hasPrevious()) {
            T t = iterator.previous();
            consumer.accept(t);
        }
    }

    @Test
    public void testInnerLoop() {
        List<Integer> list = Lists.newArrayList(1, 2, 3, 4, 5);
        List<Integer> newList = new ArrayList<>();
        higherOrder(list, newList::add);
        assertThat(newList).containsExactly(5, 4, 3, 2, 1);
    }
}
