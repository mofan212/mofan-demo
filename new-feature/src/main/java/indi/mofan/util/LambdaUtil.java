package indi.mofan.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author mofan
 * @date 2023/8/1 9:48
 */
public final class LambdaUtil {
    private LambdaUtil() {
    }

    public static <T> Function<T, List<Optional<T>>> reduceFunctions(Stream<Function<T, List<Optional<T>>>> locatorFunctions) {
        return locatorFunctions.reduce(
                t -> Collections.singletonList(Optional.ofNullable(t)),
                (fun1, fun2) -> fun1.andThen(list -> list.stream().filter(Optional::isPresent)
                        .map(Optional::get).flatMap(i -> fun2.apply(i).stream()).collect(Collectors.toList()))
        );
    }

    public static Function<JsonNode, List<Optional<JsonNode>>> arrayNode2NodeList(Function<JsonNode, List<Optional<JsonNode>>> arrayNodeFunction) {
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
}
