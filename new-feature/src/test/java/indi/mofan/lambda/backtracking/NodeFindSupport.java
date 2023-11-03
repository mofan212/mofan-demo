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

    @Override
    public void findNodes(Consumer<NodeInfo> consumer) {
        findNodes(objectNode -> {
            consumer.accept(objectNode);
            return false;
        });
    }

    private void findNodes(Predicate<NodeInfo> predicate) {
        findNodes(this.nodes, predicate, (supplier, nodes) -> supplier.getAsBoolean());
    }

    @SuppressWarnings("unchecked")
    public List<NodeInfo> findScopeNodes(String rangeIdentifier) {
        Deque<List<? extends NodeInfo>> stack = new ArrayDeque<>();
        stack.push(this.nodes);
        findNodes(this.nodes, node -> Objects.equals(rangeIdentifier, node.getNodeId()), (supplier, nodes) -> {
            stack.push(nodes);
            if (supplier.getAsBoolean()) {
                return true;
            }
            stack.pop();
            return false;
        });
        return Optional.of(stack.pop()).map(i -> (List<NodeInfo>) i).orElse(Collections.emptyList());
    }

    private boolean findNodes(List<? extends NodeInfo> nodeList, Predicate<NodeInfo> predicate, BiPredicate<BooleanSupplier, List<NodeInfo>> biPredicate) {
        for (NodeInfo node : nodeList) {
            if (findNode(node, predicate, biPredicate)) {
                return true;
            }
        }
        return false;
    }

    private boolean findNode(NodeInfo node, Predicate<NodeInfo> predicate, BiPredicate<BooleanSupplier, List<NodeInfo>> biPredicate) {
        if (node == null) {
            return false;
        }
        if (!NodeType.VIRTUAL_CONTAINER.equals(node.getNodeType()) && predicate.test(node)) {
            return true;
        }
        switch (node) {
            case VirtualContainerNode virtual -> {
                List<NodeInfo> nodeList = virtual.getNodes();
                if (biPredicate.test(() -> findNodes(nodeList, predicate, biPredicate), nodeList)) {
                    return true;
                }
            }
            case ContainerNode container -> {
                if (findNodes(container.getNodes(), predicate, biPredicate)) {
                    return true;
                }
            }
            default -> {
            }
        }
        return false;
    }
}
