package indi.mofan.lambda.backtracking.node.single;

import indi.mofan.lambda.backtracking.node.NodeType;

/**
 * @author mofan
 * @date 2023/11/3 14:54
 */
public class SingleA extends BaseSingleNode {

    public SingleA(String nodeId) {
        super(nodeId);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.SINGLE_A;
    }
}
