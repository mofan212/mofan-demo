package indi.mofan.lambda.backtracking.node.single;

import indi.mofan.lambda.backtracking.node.NodeType;

/**
 * @author mofan
 * @date 2023/11/3 16:23
 */
public class SingleC extends BaseNode {

    public SingleC(String nodeId) {
        super(nodeId);
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.SINGLE_C;
    }
}
