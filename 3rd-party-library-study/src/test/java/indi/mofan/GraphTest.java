package indi.mofan;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.Graphs;
import com.google.common.graph.MutableGraph;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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


}
