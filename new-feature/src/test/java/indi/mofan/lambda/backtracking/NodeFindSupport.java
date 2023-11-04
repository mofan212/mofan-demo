package indi.mofan.lambda.backtracking;

import indi.mofan.lambda.backtracking.node.NodeInfo;
import indi.mofan.lambda.backtracking.node.NodeType;
import indi.mofan.lambda.backtracking.node.container.ContainerNode;
import indi.mofan.lambda.backtracking.node.container.VirtualContainerNode;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author mofan
 * @date 2023/11/3 17:11
 */
public class NodeFindSupport extends NodeFindSimpleSupport {

    private NodeFindSupport(List<? extends NodeInfo> nodes) {
        super(nodes);
    }

    public static NodeFindSupport from(List<? extends NodeInfo> nodes) {
        return new NodeFindSupport(nodes);
    }

    /**
     * 获取每个节点并对它们进行处理
     *
     * @param consumer 对每个节点的处理方式
     */
    @Override
    public void findNodes(Consumer<NodeInfo> consumer) {
        findNodes(objectNode -> {
            consumer.accept(objectNode);
            // 始终返回 false，遍历完所有节点
            return false;
        });
    }

    private void findNodes(Predicate<NodeInfo> predicate) {
        findNodes(this.nodes, predicate, (supplier, nodes) -> supplier.getAsBoolean());
    }

    /**
     * 查找某个节点所在作用域中的所有节点
     *
     * @param nodeId 节点 ID，用于确定作用域
     * @return 目标作用域中的所有节点
     */
    @SuppressWarnings("unchecked")
    public List<NodeInfo> findScopeNodes(String nodeId) {
        // 遍历节点时，记录当前作用域中的节点。先进后出，使用栈。
        Deque<List<? extends NodeInfo>> stack = new ArrayDeque<>();
        // 最外层也是一个作用域
        stack.push(this.nodes);
        findNodes(this.nodes, node -> Objects.equals(nodeId, node.getNodeId()), (supplier, nodes) -> {
            // 到达一个新的作用域时，添加域中所有节点到栈中
            stack.push(nodes);
            // 如果在当前作用域中找到目标节点，立即返回
            if (supplier.getAsBoolean()) {
                return true;
            }
            // 否则弹出当前作用域中的节点
            stack.pop();
            // 返回 false，继续遍历节点
            return false;
        });
        // 如果找到目标节点 ID 所在的作用域，那么这个作用域中的所有节点就是栈顶元素
        return Optional.of(stack.pop()).map(i -> (List<NodeInfo>) i).orElse(Collections.emptyList());
    }

    /**
     * @see NodeFindSupport#findNode(NodeInfo, Predicate, BiPredicate)
     */
    private boolean findNodes(List<? extends NodeInfo> nodeList, Predicate<NodeInfo> predicate, BiPredicate<BooleanSupplier, List<NodeInfo>> biPredicate) {
        for (NodeInfo node : nodeList) {
            if (findNode(node, predicate, biPredicate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 处理每个节点的底层实现
     *
     * @param node        遍历的每个节点
     * @param predicate   对每个节点的处理与判断，返回 true 时，结束遍历
     * @param biPredicate 对虚拟容器节点的处理与判断。接收两个参数，第一个参数表示对容器内节点的处理结果，
     *                    如果返回 true，结束对容器内节点的处理；第二个参数表示容器中的所有节点。整个
     *                    谓词返回 true 时，结束遍历
     * @return 是否找到目标节点
     */
    private boolean findNode(NodeInfo node, Predicate<NodeInfo> predicate, BiPredicate<BooleanSupplier, List<NodeInfo>> biPredicate) {
        if (node == null) {
            return false;
        }
        // 忽略虚拟容器节点
        if (!NodeType.VIRTUAL_CONTAINER.equals(node.getNodeType()) && predicate.test(node)) {
            return true;
        }
        switch (node) {
            case VirtualContainerNode virtual -> {
                List<NodeInfo> nodeList = virtual.getNodes();
                // 不仅要记录容器内的节点信息，如果容器中存在目标节点，还要立即返回
                if (biPredicate.test(() -> findNodes(nodeList, predicate, biPredicate), nodeList)) {
                    return true;
                }
            }
            case ContainerNode container -> {
                if (findNodes(container.getNodes(), predicate, biPredicate)) {
                    return true;
                }
            }
            // 其他类型的节点，不做处理
            default -> {
            }
        }
        return false;
    }
}
