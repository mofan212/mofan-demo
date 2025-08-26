package indi.mofan.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
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

    public static <T> FunctionNode<T> recursiveReduce(List<FunctionNode<T>> locatorFunctions) {
        if (CollectionUtils.isEmpty(locatorFunctions)) {
            return FunctionNode.identity();
        }
        Function<T, List<Optional<T>>> fn = locatorFunctions.getFirst().getFunction();
        for (int i = 1; i < locatorFunctions.size(); i++) {
            FunctionNode<T> functionNode = locatorFunctions.get(i);
            Function<T, List<Optional<T>>> function = functionNode.getFunction();
            if (functionNode.isRecursive()) {
                // reduce 递归组件到当前组件的定位方式
                int recursiveIdx = locatorFunctions.indexOf(functionNode.getRecursiveNode());
                List<FunctionNode<T>> recursivePath = locatorFunctions.subList(recursiveIdx, i);
                // 支持递归组件套递归组件？先不支持吧。
                if (recursivePath.stream().noneMatch(FunctionNode::isRecursive)) {
                    FunctionNode<T> recursiveFunctionNode = recursiveReduce(recursivePath);
                    SelfApplicable<T, List<Optional<T>>> recursiveFunction = (self, node) -> {
                        List<T> list = recursiveFunctionNode.getFunction().apply(node).stream()
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .toList();
                        if (list.isEmpty()) {
                            return functionNode.getFunction().apply(node);
                        } else {
                            return list.stream()
                                    .flatMap(x -> self.apply(self, x).stream())
                                    .toList();
                        }
                    };
                    // 转换当前 function
                    function = recursiveFunction.toFunction();
                }
            }
            fn = fn.andThen(operate(function));
        }
        return FunctionNode.of(fn);
    }

    private interface SelfApplicable<T, R> {
        R apply(SelfApplicable<T, R> selfApplicable, T arg);

        default Function<T, R> toFunction() {
            return i -> apply(this, i);
        }
    }

    public static <T> UnaryOperator<List<Optional<T>>> operate(Function<T, List<Optional<T>>> function) {
        return i -> i.stream().filter(Optional::isPresent).map(Optional::get).flatMap(j -> function.apply(j).stream()).toList();
    }

    @Getter
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class FunctionNode<T> {
        private final boolean recursive;
        private final Function<T, List<Optional<T>>> function;
        private FunctionNode<T> recursiveNode;

        public static <T> FunctionNode<T> of(Function<T, List<Optional<T>>> function) {
            return new FunctionNode<>(false, function);
        }

        public static <T> FunctionNode<T> ofRecursive(Function<T, List<Optional<T>>> function,
                                                      FunctionNode<T> recursiveNode) {
            return new FunctionNode<>(true, function, recursiveNode);
        }

        public static <T> FunctionNode<T> identity() {
            return new FunctionNode<>(false, i -> Collections.singletonList(Optional.ofNullable(i)));
        }
    }
}
