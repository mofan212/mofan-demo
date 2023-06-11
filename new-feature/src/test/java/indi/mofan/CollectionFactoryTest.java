package indi.mofan;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author mofan
 * @date 2023/6/11 21:07
 */
public class CollectionFactoryTest implements WithAssertions {
    @Test
    public void test() {
        var list = List.of("1", "2", "3");
        assertThat(list).hasSize(3);

        try {
            list.add("4");
            Assertions.fail();
        } catch (Exception e) {
            assertThat(e).isExactlyInstanceOf(UnsupportedOperationException.class);
        }

        // set 不保证顺序
        var set = Set.of(1, 2, 3, 4);
        assertThat(set).hasSize(4);

        var map = Map.of("1", 1, "2", 2);
        assertThat(map).hasSize(2);
    }
}
