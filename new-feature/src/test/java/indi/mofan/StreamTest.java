package indi.mofan;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    @Test
    public void testAccumulateFunction() {
        Function<Integer, List<Optional<Integer>>> fun1 = integer -> List.of(Optional.of(integer + 1));
        Function<Integer, List<Optional<Integer>>> fun2 = integer -> List.of(Optional.of(integer * 2));

        Function<Integer, List<Optional<Integer>>> function = Stream.of(fun1, fun2).reduce(integer -> List.of(Optional.of(integer)),
                (fn1, fn2) -> fn1.andThen(list -> list.stream().filter(Optional::isPresent).map(Optional::get).flatMap(i -> fn2.apply(i).stream()).toList()));

        List<Integer> list = function.apply(2).stream().filter(Optional::isPresent).map(Optional::get).toList();
        assertThat(list).containsOnly(6);
    }

    @Test
    public void testIterable() {
        List<JsonNode> textNodes = List.of(new TextNode("a"), new TextNode("b"), new TextNode("c"));
        ArrayNode arrayNode = new ArrayNode(new ObjectMapper().getNodeFactory(), textNodes);
        List<String> list = StreamSupport.stream(arrayNode.spliterator(), false)
                .filter(JsonNode::isTextual)
                .map(JsonNode::asText)
                .toList();
        assertThat(list).containsOnly("a", "b", "c");
    }
}
