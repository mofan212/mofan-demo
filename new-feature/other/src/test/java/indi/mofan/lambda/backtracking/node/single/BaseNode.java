package indi.mofan.lambda.backtracking.node.single;

import indi.mofan.lambda.backtracking.node.NodeInfo;
import indi.mofan.lambda.backtracking.node.NodeType;

/**
 * @author mofan
 * @date 2023/11/3 14:56
 */
public abstract class BaseNode implements NodeInfo {
    protected String nodeId;
    protected NodeType nodeType;

    protected BaseNode(String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public String getNodeId() {
        return this.nodeId;
    }

    @Override
    public NodeType getNodeType() {
        return this.nodeType;
    }
}
