package indi.mofan.graph;

import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

/**
 * @author mofan
 * @date 2023/10/27 10:20
 */
@SuppressWarnings("UnstableApiUsage")
public class GraphTraverserTest implements WithAssertions {

    private Graph<String> buildDirectedGraph() {
        MutableGraph<String> graph = GraphBuilder.directed().expectedNodeCount(6).build();
        graph.putEdge("d", "a");
        graph.putEdge("a", "e");
        graph.putEdge("a", "b");
        graph.putEdge("b", "e");
        graph.putEdge("e", "c");
        graph.putEdge("e", "f");
        graph.putEdge("f", "c");
        graph.putEdge("c", "b");
        return graph;
    }

    @Test
    public void testDfs() {
        Graph<String> graph = buildDirectedGraph();
        Traverser<String> traverser = Traverser.forGraph(graph);
        // 从 a 开始
        Iterable<String> preOrderStartA = traverser.depthFirstPreOrder("a");
        // 里面没有 d
        assertThat(preOrderStartA).isNotIn("d");

        // 从 d 开始
        Iterable<String> preOrderStartD = traverser.depthFirstPreOrder("d");
        assertThat(preOrderStartD).hasSize(6).contains("a", "b", "c", "d", "e", "f");
    }
}
