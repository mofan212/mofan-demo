package indi.mofan.lambda.backtracking.node.container;

import indi.mofan.lambda.backtracking.node.NodeType;

import java.util.List;

/**
 * @author mofan
 * @date 2023/11/3 16:23
 */
public class ContainerNode extends BaseContainerNode {

    public ContainerNode(String nodeId, List<VirtualContainerNode> nodes) {
        super(nodeId, nodes);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.CONTAINER;
    }
}
