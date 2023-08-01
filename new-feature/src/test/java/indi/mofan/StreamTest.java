package indi.mofan;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
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

    private Function<JsonNode, List<Optional<JsonNode>>> arrayNode2NodeList(Function<JsonNode, List<Optional<JsonNode>>> arrayNodeFunction) {
        return arrayNodeFunction.andThen(list ->
                list.stream()
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(JsonNode::isArray)
                        .map(i -> (ArrayNode) i)
                        .flatMap(i -> StreamSupport.stream(i.spliterator(), false).map(Optional::of))
                        .collect(Collectors.toList())
        );
    }

    @Test
    @SneakyThrows
    public void testArrayNode2NodeList() {
        //language=JSON
        String json = """
                {
                  "array": [
                    {
                      "str": "str1"
                    },
                    {
                      "str": "str2"
                    }
                  ]
                }
                                """;
        Function<JsonNode, List<Optional<JsonNode>>> originFunction = jsonNode -> List.of(Optional.ofNullable(jsonNode.get("array")));
        JsonMapper mapper = JsonMapper.builder().build();
        JsonNode jsonNode = mapper.readTree(json);
        List<Optional<JsonNode>> list = arrayNode2NodeList(originFunction).apply(jsonNode);
        assertThat(list).hasSize(2)
                .filteredOn(Optional::isPresent)
                .hasSize(2)
                .map(Optional::get)
                .map(i -> i.get("str").asText())
                .containsExactly("str1", "str2");
    }
}
