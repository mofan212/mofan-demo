package indi.mofan.util;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mofan
 * @date 2022/10/9 13:50
 */
public class GraphUtil {

    public static <N> Graph<N> buildGraph(Map<N, Collection<N>> map) {
        // 定义一个有向图
        MutableGraph<N> callGraph = GraphBuilder.directed()
                .nodeOrder(ElementOrder.<N>insertion())
                .expectedNodeCount(map.size() << 1)
                .allowsSelfLoops(false)
                .build();
        // 对有向图添加边
        for (Map.Entry<N, Collection<N>> entry : map.entrySet()) {
            N parentMfId = entry.getKey();
            Collection<N> value = entry.getValue();
            if (CollectionUtils.isEmpty(value)) {
                callGraph.addNode(parentMfId);
            } else {
                for (N subMfId : value) {
                    callGraph.putEdge(parentMfId, subMfId);
                }
            }
        }
        return callGraph;
    }

    /**
     * 获取两个节点之间的所有路径
     *
     * @param graph     图
     * @param startNode 起始节点
     * @param endNode   结束节点
     * @param <T>       节点类型
     * @return 所有路径信息（每条路径都是从开始节点到结束节点）
     */
    public static <T> List<List<T>> getAllPath(Graph<T> graph, T startNode, T endNode) {
        Deque<T> main = new ArrayDeque<>();
        Deque<Collection<T>> secondary = new ArrayDeque<>();

        // 所有路径
        List<List<T>> path = new ArrayList<>();

        main.push(startNode);
        // 获取开始节点的所有后继节点并将塞入辅栈中（new 一个 Set，防止 UnsupportedOperationException）
        secondary.push(new HashSet<>(graph.successors(startNode)));

        while (CollectionUtils.isNotEmpty(main)) {
            Collection<T> secondaryPeek = secondary.peek();
            if (CollectionUtils.isNotEmpty(secondaryPeek)) {
                Optional<T> optional = secondaryPeek.stream().findAny();
                if (optional.isPresent()) {
                    T t = optional.get();
                    main.push(t);
                    secondaryPeek.remove(t);

                    Set<T> successors = graph.successors(t);
                    Set<T> collect = successors.stream().filter(i -> !main.contains(i)).collect(Collectors.toSet());
                    secondary.push(collect);
                }
            } else { // 削栈
                cutTwoStack(main, secondary);
            }

            if (Objects.equals(main.peek(), endNode)) {
                List<T> onePath = new ArrayList<>();
                // ArrayDeque 的 push() 是将元素放到第一个，因此需要倒着迭代一下
                main.descendingIterator().forEachRemaining(onePath::add);
                path.add(onePath);
                // 再次削栈
                cutTwoStack(main, secondary);
            }
        }
        return path;
    }

    /**
     * 削栈（移除两个栈的顶层节点）
     *
     * @param mainStack      主栈
     * @param secondaryStack 辅栈
     * @param <T>            节点类型
     */
    private static <T> void cutTwoStack(Deque<T> mainStack, Deque<Collection<T>> secondaryStack) {
        mainStack.pop();
        secondaryStack.pop();
    }

    /**
     * 获取有向图中所有的环信息
     *
     * @param graph 有向图
     * @param <T>   节点类型
     * @return 图中环的信息
     */
    public static <T> List<List<T>> findAllCycles(Graph<T> graph) {
        List<List<T>> cycles = new ArrayList<>();
        // 访问过的节点
        Set<T> visited = new HashSet<>();
        for (T node : graph.nodes()) {
            if (!visited.contains(node)) {
                // 记录当前节点路径
                List<T> path = new ArrayList<>();
                findCycles(graph, node, path, visited, cycles);
            }
        }
        return cycles;
    }

    /**
     * 获取图中某个节点路径上可能的环信息
     *
     * @param graph   有向图
     * @param node    某个节点
     * @param path    记录遍历节点的路径
     * @param visited 访问过的节点集合
     * @param cycles  环信息
     * @param <T>     节点类型
     */
    private static <T> void findCycles(Graph<T> graph, T node, List<T> path,
                                       Set<T> visited, List<List<T>> cycles) {
        visited.add(node);
        path.add(node);
        // 遍历当前节点所有后继节点
        for (T successor : graph.successors(node)) {
            // 如果路径中包含当前节点，证明有环
            if (path.contains(successor)) {
                // 获取环的起始节点的位置
                int index = path.indexOf(successor);
                // 获取环信息
                List<T> cycle = path.subList(index, path.size());
                // 加到结果集里
                cycles.add(new ArrayList<>(cycle));
            } else if (!visited.contains(successor)) {
                // 对后继节点执行相同操作
                findCycles(graph, successor, path, visited, cycles);
            }
        }
        // 回溯后，移除当前节点
        path.remove(node);
    }
}
