package indi.mofan.lambda.backtracking.node.container;

import indi.mofan.lambda.backtracking.node.NodeInfo;
import indi.mofan.lambda.backtracking.node.single.BaseSingleNode;

import java.util.Collections;
import java.util.List;

/**
 * @author mofan
 * @date 2023/11/3 16:23
 */
public abstract class BaseContainerNode extends BaseSingleNode {
    protected List<? extends NodeInfo> nodes;

    protected BaseContainerNode(String nodeId, List<? extends NodeInfo> nodes) {
        super(nodeId);
        this.nodes = nodes;
    }

    public List<? extends NodeInfo> getNodes() {
        if (this.nodes == null) {
            return Collections.emptyList();
        }
        return nodes;
    }

    public void setNodes(List<NodeInfo> nodes) {
        this.nodes = nodes;
    }
}
