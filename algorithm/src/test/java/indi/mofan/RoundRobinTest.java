package indi.mofan;


import indi.mofan.roundrobin.SimpleWeightedRoundRobin;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

/**
 * @author mofan
 * @date 2025/8/29 14:50
 */
public class RoundRobinTest implements WithAssertions {
    @Test
    public void testSimpleWeightedRoundRobin() {
        SimpleWeightedRoundRobin robin = new SimpleWeightedRoundRobin(List.of(
                new SimpleWeightedRoundRobin.Server("A", 3),
                new SimpleWeightedRoundRobin.Server("B", 2),
                new SimpleWeightedRoundRobin.Server("C", 1)
        ));
        List<String> serverNameList = IntStream.range(0, 13)
                .mapToObj(i -> robin.getNextServer())
                .map(SimpleWeightedRoundRobin.Server::getName)
                .toList();
        assertThat(serverNameList).containsExactly(
                "C", "B", "A", "B", "A", "A",
                "C", "B", "A", "B", "A", "A",
                "C"
        );
    }
}
