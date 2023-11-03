package indi.mofan.lambda.backtracking.node;

/**
 * @author mofan
 * @date 2023/11/3 14:53
 */
public interface NodeInfo {

    /**
     * 节点标识
     *
     * @return 节点标识
     */
    String getNodeId();

    /**
     * 节点类型
     *
     * @return 节点类型
     */
    NodeType getNodeType();
}
