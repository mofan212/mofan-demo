package indi.mofan;

/**
 * @author mofan
 * @date 2024/3/8 22:39
 */
public interface Node {
    /**
     * 节点名称
     */
    String name();

    /**
     * 以当前节点为根节点的子树所含节点的数量
     */
    int getTreeSize();

    /**
     * 获取左节点
     */
    Node left();

    /**
     * 获取右节点
     */
    Node right();

    /**
     * 前序遍历
     */
    void walk();
}
