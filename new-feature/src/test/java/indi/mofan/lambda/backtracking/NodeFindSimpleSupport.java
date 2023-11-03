package indi.mofan.lambda.backtracking;

import indi.mofan.lambda.backtracking.node.NodeInfo;
import indi.mofan.lambda.backtracking.node.container.BaseContainerNode;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author mofan
 * @date 2023/11/3 16:32
 */
public class NodeFindSimpleSupport {

    private final List<? extends NodeInfo> nodes;

    protected NodeFindSimpleSupport(List<? extends NodeInfo> nodes) {
        this.nodes = nodes;
    }

    public static NodeFindSimpleSupport from(List<? extends NodeInfo> nodes) {
        return new NodeFindSimpleSupport(nodes);
    }

    public void findNodes(Consumer<NodeInfo> consumer) {
        for (NodeInfo nodeInfo : CollectionUtils.emptyIfNull(this.nodes)) {
            findNode(nodeInfo, consumer);
        }
    }

    private void findNode(NodeInfo nodeInfo, Consumer<NodeInfo> consumer) {
        if (nodeInfo == null) {
            return;
        }
        // 处理每个节点
        consumer.accept(nodeInfo);
        // 处理容器节点
        if (nodeInfo instanceof BaseContainerNode container) {
            from(container.getNodes()).findNodes(consumer);
        }
    }
}
