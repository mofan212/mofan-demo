package indi.mofan.lambda.backtracking.node.single;

import indi.mofan.lambda.backtracking.node.NodeType;

/**
 * @author mofan
 * @date 2023/11/3 16:22
 */
public class SingleB extends BaseSingleNode {

    public SingleB(String nodeId) {
        super(nodeId);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.SINGLE_B;
    }
}
