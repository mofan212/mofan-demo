package indi.mofan;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mofan
 * @date 2023/7/14 22:51
 */
public class StreamTest implements WithAssertions {
    @Test
    public void testMatch() {
        List<String> list = new ArrayList<>();
        assertThat(list.stream().allMatch("test"::equals)).isTrue();
        assertThat(list.stream().allMatch(i -> !"test".equals(i))).isTrue();
        assertThat(list.stream().noneMatch("test"::equals)).isTrue();
        assertThat(list.stream().noneMatch(i -> !"test".equals(i))).isTrue();
        assertThat(list.stream().anyMatch(i -> !"test".equals(i))).isFalse();
        assertThat(list.stream().anyMatch("test"::equals)).isFalse();
    }
}
