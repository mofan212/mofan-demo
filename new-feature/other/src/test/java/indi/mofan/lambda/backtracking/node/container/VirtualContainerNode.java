package indi.mofan.lambda.backtracking.node.container;

import indi.mofan.lambda.backtracking.node.NodeInfo;
import indi.mofan.lambda.backtracking.node.NodeType;

import java.util.List;

/**
 * @author mofan
 * @date 2023/11/3 16:25
 */
public class VirtualContainerNode extends BaseContainerNode {

    public VirtualContainerNode(String nodeId, List<NodeInfo> nodes) {
        super(nodeId, nodes);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.VIRTUAL_CONTAINER;
    }
}
