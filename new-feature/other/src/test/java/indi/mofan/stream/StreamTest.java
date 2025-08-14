package indi.mofan.stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static indi.mofan.util.LambdaUtil.*;

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

    @Test
    @SneakyThrows
    public void testArrayNode2NodeList() {
        // language=JSON
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

    @Test
    @SneakyThrows
    public void testReduceFunctions() {
        Function<JsonNode, List<Optional<JsonNode>>> function = jsonNode -> List.of(Optional.ofNullable(jsonNode.get("object")));
        // language=JSON
        String json = """
                {
                  "integer": 123,
                  "object": {
                    "str": "str"
                  }
                }
                """;
        JsonMapper mapper = JsonMapper.builder().build();
        JsonNode jsonNode = mapper.readTree(json);
        List<Optional<JsonNode>> firstValues = function.apply(jsonNode);
        assertThat(firstValues).hasSize(1)
                .filteredOn(Optional::isPresent)
                .map(Optional::get)
                .map(i -> i.get("str"))
                .filteredOn(i -> i instanceof TextNode)
                .map(JsonNode::asText)
                .containsOnly("str");
        List<Optional<JsonNode>> secondValues = reduceFunctions(Stream.of(function)).apply(jsonNode);
        assertThat(secondValues).hasSize(1)
                .filteredOn(Optional::isPresent)
                .map(Optional::get)
                .map(i -> i.get("str").asText())
                .containsOnly("str");
    }

    @Test
    @SneakyThrows
    public void testReduceFunctionsWithCycle() {
        // language=JSON
        String json = """
                {
                  "X": {
                    "Y": {
                      "A": {
                        "B": {
                          "A": {
                            "B": {
                              "A": {
                                "B": {
                                  "C": {
                                    "D": {
                                      "E": "target value"
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
                """;
        Function<JsonNode, List<Optional<JsonNode>>> functionX = node -> List.of(Optional.ofNullable(node.get("X")));
        FunctionNode<JsonNode> nodeX = FunctionNode.of(functionX);

        Function<JsonNode, List<Optional<JsonNode>>> functionY = node -> List.of(Optional.ofNullable(node.get("Y")));
        FunctionNode<JsonNode> nodeY = FunctionNode.of(functionY);

        Function<JsonNode, List<Optional<JsonNode>>> functionA = node -> List.of(Optional.ofNullable(node.get("A")));
        FunctionNode<JsonNode> nodeA = FunctionNode.of(functionA);

        Function<JsonNode, List<Optional<JsonNode>>> functionB = node -> List.of(Optional.ofNullable(node.get("B")));
        FunctionNode<JsonNode> nodeB = FunctionNode.of(functionB);

        // 怎么得到 aToBFunc？
        // Function<JsonNode, List<Optional<JsonNode>>> aToBFunc = functionA.andThen(operate(functionB));
        Function<JsonNode, List<Optional<JsonNode>>> functionC = node -> List.of(Optional.ofNullable(node.get("C")));
        // FunctionNode<JsonNode> nodeC = FunctionNode.ofRecursive(functionC, aToBFunc);
        FunctionNode<JsonNode> nodeC = FunctionNode.ofRecursive(functionC, nodeA);

        Function<JsonNode, List<Optional<JsonNode>>> functionD = node -> List.of(Optional.ofNullable(node.get("D")));
        FunctionNode<JsonNode> nodeD = FunctionNode.of(functionD);

        Function<JsonNode, List<Optional<JsonNode>>> functionE = node -> List.of(Optional.ofNullable(node.get("E")));
        FunctionNode<JsonNode> nodeE = FunctionNode.of(functionE);

        FunctionNode<JsonNode> node = recursiveReduce(List.of(nodeX, nodeY, nodeA, nodeB, nodeC, nodeD, nodeE));

        JsonNode jsonNode = JsonMapper.builder().build().readTree(json);

        List<Optional<JsonNode>> list = node.getFunction().apply(jsonNode);
        assertThat(list).hasSize(1)
                .filteredOn(Optional::isPresent)
                .map(Optional::get)
                .filteredOn(i -> i instanceof TextNode)
                .map(JsonNode::asText)
                .containsOnly("target value");
    }

    @Test
    public void testPeek() {
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6);
        // 不会打印任何值
        long count = list.stream().peek(System.out::println).count();
        assertThat(count).isEqualTo(6);
        System.out.println("==>");
        // 只打印一次 1
        list.stream().peek(System.out::println).findFirst().ifPresent(one -> assertThat(one).isEqualTo(1));
    }

    @Test
    public void testPartitioningBy() {
        List<Integer> list = List.of(1, 2, 3, 4, 5);
        Map<Boolean, List<Integer>> map = list.stream().collect(Collectors.partitioningBy(i -> i > 10));
        // 大于 10 的，就算没有，也不会是 null
        assertThat(map.get(Boolean.TRUE)).isNotNull().isEmpty();
        // 不大于 10 的
        assertThat(map.get(Boolean.FALSE)).isNotEmpty().containsExactly(1, 2, 3, 4, 5);
    }

    @Getter
    @AllArgsConstructor
    private static class Person {
        private String name;
        private Integer age;
    }

    @Test
    public void testGroupingBy() {
        Person person = new Person("mofan", 21);
        Map<Integer, List<Person>> map = Stream.of(person)
                .collect(Collectors.groupingBy(Person::getAge));
        // 年龄 18 的人，没有就是 null
        assertThat(map.get(18)).isNull();
        // 年龄等于 21 的
        assertThat(map.get(21)).isNotNull().hasSize(1);
    }

    @Test
    public void testDistinctByNotNullName() {
        Person p1 = new Person("mofan", 21);
        Person p2 = new Person("mofan", 22);
        Person p3 = new Person("", 23);
        Person p4 = new Person(null, 24);

        List<Person> newList = Stream.of(p1, p2, p3, p4)
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                i -> StringUtils.isNotEmpty(i.getName()) ? i.getName() : String.valueOf(i.getAge()),
                                Function.identity(),
                                (o1, o2) -> o1
                        ),
                        i -> new ArrayList<>(i.values()))
                );
        assertThat(newList).hasSize(3)
                .extracting(Person::getName)
                .containsExactlyInAnyOrder("mofan", "", null);
    }
}
