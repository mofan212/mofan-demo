package indi.mofan.lambda.backtracking.node.container;

import indi.mofan.lambda.backtracking.node.NodeInfo;
import indi.mofan.lambda.backtracking.node.single.BaseNode;

import java.util.Collections;
import java.util.List;

/**
 * @author mofan
 * @date 2023/11/3 16:23
 */
public abstract class BaseContainerNode extends BaseNode {
    protected List<? extends NodeInfo> nodes;

    protected BaseContainerNode(String nodeId, List<? extends NodeInfo> nodes) {
        super(nodeId);
        this.nodes = nodes;
    }

    @SuppressWarnings("unchecked")
    public List<NodeInfo> getNodes() {
        if (this.nodes == null) {
            return Collections.emptyList();
        }
        return (List<NodeInfo>) nodes;
    }
}
