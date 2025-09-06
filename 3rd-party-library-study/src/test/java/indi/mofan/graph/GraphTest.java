package indi.mofan.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import indi.mofan.util.CallChainUtil;
import indi.mofan.util.GraphUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mofan
 * @date 2022/10/8 17:31
 */
public class GraphTest implements WithAssertions {
    @Test
    public void testGraph() {
        MutableGraph<Integer> graph = GraphBuilder.directed()
                .nodeOrder(ElementOrder.<Integer>insertion())
                .expectedNodeCount(4)
                .allowsSelfLoops(true)
                .build();

        graph.putEdge(1, 2);
        graph.putEdge(1, 3);
        graph.putEdge(2, 3);
        graph.putEdge(2, 2);
        graph.putEdge(2, 4);

        Graph<Integer> transposeGraph = Graphs.transpose(graph);
        Set<Integer> integers = Graphs.reachableNodes(transposeGraph, 4);
        Assertions.assertTrue(integers.containsAll(new HashSet<>(Arrays.asList(1, 2, 4))));
    }

    @Test
    public void testGetAllPath() {
        MutableGraph<Integer> graph = GraphBuilder.undirected()
                .nodeOrder(ElementOrder.<Integer>insertion())
                .expectedNodeCount(8)
                .allowsSelfLoops(false)
                .build();

        graph.putEdge(3, 7);
        graph.putEdge(3, 1);
        graph.putEdge(1, 0);
        graph.putEdge(1, 4);
        graph.putEdge(7, 4);
        graph.putEdge(4, 5);
        graph.putEdge(0, 2);
        graph.putEdge(2, 5);
        graph.putEdge(2, 6);
        graph.putEdge(5, 6);
        graph.putEdge(6, 8);

        List<List<Integer>> allPath = GraphUtil.getAllPath(graph, 3, 6);
        assertThat(allPath).containsExactlyInAnyOrder(
                List.of(3, 1, 0, 2, 6),
                List.of(3, 1, 0, 2, 5, 6),
                List.of(3, 1, 4, 5, 6),
                List.of(3, 1, 4, 5, 2, 6),
                List.of(3, 7, 4, 5, 2, 6),
                List.of(3, 7, 4, 5, 6),
                List.of(3, 7, 4, 1, 0, 2, 6),
                List.of(3, 7, 4, 1, 0, 2, 5, 6)
        );
    }

    @Test
    @Disabled
    public void testGetCyclePath() {
        MutableGraph<Integer> graph = GraphBuilder.directed()
                .nodeOrder(ElementOrder.<Integer>insertion())
                .expectedNodeCount(3)
                .allowsSelfLoops(false)
                .build();

        graph.putEdge(1, 2);
        graph.putEdge(2, 3);
        graph.putEdge(3, 1);
        graph.putEdge(4, 1);

        final int target = 1;

        // 获取谁调用了 1
        Graph<Integer> transpose = Graphs.transpose(graph);
        Set<Integer> transposeReachableNodes = Graphs.reachableNodes(transpose, target);

        // 调用了 1 的，并且入度为 0：底层节点
        Set<Integer> sourceSet = transposeReachableNodes.stream()
                .filter(i -> graph.inDegree(i) == 0)
                .collect(Collectors.toSet());

        // 获取顶层节点到目标节点的路径
        List<List<Integer>> path = sourceSet.stream()
                .map(i -> GraphUtil.getAllPath(graph, i, target))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        // 调用关系中是否存在间接自己调用自己的情况
        boolean hasCycle = transposeReachableNodes.contains(target);

        if (hasCycle) {
            Set<Integer> reachableNodes = Graphs.reachableNodes(graph, target);

            // 获取组成环的节点（不包含目标节点）
            Set<Integer> cycleNodes = transposeReachableNodes.stream()
                    .filter(reachableNodes::contains)
                    .filter(i -> !Objects.equals(i, target))
                    .collect(Collectors.toSet());

            // 获取环节点到目标节点的最长路径
            cycleNodes.stream()
                    .map(i -> GraphUtil.getAllPath(graph, i, target))
                    .flatMap(Collection::stream)
                    .max(Comparator.comparingInt(List::size))
                    .ifPresent(i -> {
                        i.add(0, target);
                        path.add(i);
                    });

            path.forEach(System.out::println);

        }
    }

    @Test
    @SneakyThrows
    public void testRestoreTreeByPath() {
        List<Integer> one = new ArrayList<>(Arrays.asList(2, 3, 5));
        List<Integer> two = new ArrayList<>(Arrays.asList(2, 3, 6, 7));
        List<Integer> three = new ArrayList<>(Arrays.asList(2, 4, 3, 6, 7));
        List<Integer> four = new ArrayList<>(Arrays.asList(2, 4, 3, 5));
        List<List<Integer>> allPath = new ArrayList<>(Arrays.asList(one, two, three, four));

        // init map
        Map<Integer, Tree> treeMap = new HashMap<>();
        treeMap.put(2, new Tree(2, new ArrayList<>()));
        treeMap.put(3, new Tree(3, new ArrayList<>()));
        treeMap.put(4, new Tree(4, new ArrayList<>()));
        treeMap.put(5, new Tree(5, new ArrayList<>()));
        treeMap.put(6, new Tree(6, new ArrayList<>()));
        treeMap.put(7, new Tree(7, new ArrayList<>()));

        int index = 1;
        int maxSize = allPath.stream().mapToInt(List::size).max().orElse(0);
        int maxIndex = maxSize - 1;
        while (index <= maxIndex) {
            for (List<Integer> path : allPath) {
                int parentIndex = index - 1;
                if (parentIndex >= path.size() - 1) {
                    continue;
                }
                // 获取父
                Integer parent = path.get(parentIndex);
                Tree parentTree = treeMap.get(parent);
                // 设置子
                List<Integer> collect = parentTree.getChildren().stream().map(Tree::getNum).toList();
                Integer next = path.get(index);
                if (!collect.contains(next)) {
                    parentTree.getChildren().add(treeMap.get(next));
                }
            }
            index++;
        }

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(treeMap.get(2)));
    }

    @Test
    public void testGetPathByCallChainMap_1() {
        // init chain map
        Map<Integer, List<Integer>> map = new HashMap<>();
        map.put(2, new ArrayList<>(Arrays.asList(3, 4)));
        map.put(4, new ArrayList<>(Collections.singletonList(3)));
        map.put(3, new ArrayList<>(Arrays.asList(5, 6)));
        map.put(5, new ArrayList<>(Collections.emptyList()));
        map.put(6, new ArrayList<>(Collections.singletonList(7)));
        map.put(7, new ArrayList<>(Collections.emptyList()));

        CallChainUtil.getPathByCallChainMap(map, 2).forEach(System.out::println);
    }

    @Test
    public void testGetPathByCallChainMap_2() {
        // init chain map
        Map<Integer, List<Integer>> map = new HashMap<>();
        map.put(3, new ArrayList<>(Arrays.asList(1, 7)));
        map.put(1, new ArrayList<>(Arrays.asList(0, 4)));
        map.put(0, new ArrayList<>(Collections.singletonList(2)));
        map.put(2, new ArrayList<>(Arrays.asList(5, 6)));
        map.put(6, new ArrayList<>(Collections.singletonList(8)));
        map.put(8, new ArrayList<>(Collections.emptyList()));
        map.put(5, new ArrayList<>(Collections.singletonList(6)));
        map.put(4, new ArrayList<>(Collections.singletonList(5)));
        map.put(7, new ArrayList<>(Collections.singletonList(4)));

        CallChainUtil.getPathByCallChainMap(map, 3).forEach(System.out::println);
    }

    @Test
    public void testGetUpstreamPath() {
        MutableGraph<String> graph = GraphBuilder.directed()
                .nodeOrder(ElementOrder.<String>insertion())
                .expectedNodeCount(8)
                .allowsSelfLoops(false)
                .build();

        graph.putEdge("A", "B");
        graph.putEdge("B", "C");
        graph.putEdge("C", "D");
        graph.putEdge("C", "E");
        graph.putEdge("D", "A");
        graph.putEdge("A", "B");
        graph.putEdge("D", "G");
        graph.putEdge("G", "C");
        graph.putEdge("F", "C");
        graph.putEdge("H", "E");

        String targetNode = "C";

        List<List<String>> path = new ArrayList<>();

        Deque<String> main = new ArrayDeque<>();
        main.push(targetNode);
        Deque<List<String>> secondary = new ArrayDeque<>();
        secondary.push(new ArrayList<>(graph.predecessors(targetNode)));
        while (!secondary.isEmpty()) {
            List<String> peek = secondary.peek();
            if (CollectionUtils.isNotEmpty(peek)) {
                Optional<String> any = peek.stream().findAny();
                if (any.isPresent()) {
                    String s = any.get();
                    main.push(s);
                    peek.remove(s);

                    Set<String> predecessors = graph.predecessors(s);
                    if (CollectionUtils.isEmpty(predecessors)) {
                        addPath(path, main, null);
                        main.pop();
                    } else {
                        List<String> collect = predecessors.stream().filter(i -> !main.contains(i)).collect(Collectors.toList());
                        if (collect.size() == predecessors.size()) {
                            secondary.push(collect);
                        } else {
                            List<String> sameNodeList = predecessors.stream().filter(main::contains).collect(Collectors.toList());
                            addPath(path, main, sameNodeList);
                            if (CollectionUtils.isNotEmpty(collect)) {
                                secondary.push(collect);
                            } else {
                                main.pop();
                            }
                        }
                    }
                }
            } else {
                main.pop();
                secondary.pop();
            }
        }

        path.forEach(System.out::println);
    }

    private static <T> void addPath(List<List<T>> path, Deque<T> main, Collection<T> cycleStarts) {
        List<T> list = new ArrayList<>();
        main.iterator().forEachRemaining(list::add);
        if (CollectionUtils.isNotEmpty(cycleStarts)) {
            list.addAll(0, cycleStarts);
        }
        path.add(list);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class Tree {
        private int num;
        private List<Tree> children;
    }

    @Test
    public void testSingleNode() {
        Map<Long, Collection<Long>> callChainMap = new HashMap<>();
        callChainMap.put(1L, Arrays.asList(2L, 3L));
        callChainMap.put(999L, Collections.emptyList());
        Graph<Long> callGraph = GraphUtil.buildGraph(callChainMap);

        List<Long> noInDegreeNodes = callGraph.nodes().stream()
                .filter(i -> callGraph.inDegree(i) == 0)
                .toList();
        Assertions.assertEquals(2, noInDegreeNodes.size());
    }
}
