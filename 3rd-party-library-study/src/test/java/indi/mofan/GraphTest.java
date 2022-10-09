package indi.mofan;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mofan
 * @date 2022/10/8 17:31
 */
public class GraphTest {
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
        Assert.assertTrue(integers.containsAll(new HashSet<>(Arrays.asList(1, 2, 4))));
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
        for (List<Integer> path : allPath) {
            System.out.println(path);
        }
    }
    
    @Test
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
}
