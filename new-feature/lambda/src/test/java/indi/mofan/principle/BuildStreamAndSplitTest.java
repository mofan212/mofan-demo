package indi.mofan.principle;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Spliterator;

/**
 * @author mofan
 * @date 2024/7/20 17:21
 */
public class BuildStreamAndSplitTest implements WithAssertions {

    List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

    @Test
    public void testTryAdvance() {
        Spliterator<Integer> spliterator = list.spliterator();
        // 消费下一个元素
        spliterator.tryAdvance(i -> assertThat(i).isEqualTo(1));
        spliterator.tryAdvance(i -> assertThat(i).isEqualTo(2));
        spliterator.tryAdvance(i -> assertThat(i).isEqualTo(3));

        // 消费剩余元素
        spliterator.forEachRemaining(i -> assertThat(i).isGreaterThanOrEqualTo(4).isLessThanOrEqualTo(9));
    }

    @Test
    public void testTrySplit() {
        Spliterator<Integer> sp1 = list.spliterator();
        // 切一半
        Spliterator<Integer> sp2 = sp1.trySplit();
        // 对 sp2 再切一半
        Spliterator<Integer> sp3 = sp2.trySplit();

        // [5, 9]
        sp1.forEachRemaining(i -> assertThat(i).isGreaterThanOrEqualTo(5).isLessThanOrEqualTo(9));
        // [3, 4]
        sp2.forEachRemaining(i -> assertThat(i).isGreaterThanOrEqualTo(3).isLessThanOrEqualTo(4));
        // [1, 2]
        sp3.forEachRemaining(i -> assertThat(i).isGreaterThanOrEqualTo(1).isLessThanOrEqualTo(2));
    }
}
