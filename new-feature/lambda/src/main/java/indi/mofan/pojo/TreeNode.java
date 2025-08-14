package indi.mofan.pojo;

import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

/**
 * @author mofan
 * @date 2024/7/10 14:15
 */
public class TreeNode {
    @Getter
    int val;
    TreeNode left;
    TreeNode right;

    public TreeNode(int val) {
        this.val = val;
    }

    public TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }

    public enum TraversalType {
        /**
         * 前序
         */
        PRE,
        /**
         * 中序
         */
        IN,
        /**
         * 后序
         */
        POST
    }

    public static void recursiveTraversal(TreeNode root, TraversalType type, Consumer<TreeNode> consumer) {
        if (root == null) return;
        if (type == TraversalType.PRE) {
            consumer.accept(root);
        }
        recursiveTraversal(root.left, type, consumer);
        if (type == TraversalType.IN) {
            consumer.accept(root);
        }
        recursiveTraversal(root.right, type, consumer);
        if (type == TraversalType.POST) {
            consumer.accept(root);
        }
    }

    public static void traverse(TreeNode root, TraversalType type, Consumer<TreeNode> consumer) {
        // 用来记住回去的路
        Deque<TreeNode> stack = new ArrayDeque<>();
        // 当前节点
        TreeNode curr = root;
        // 上次处理的节点
        TreeNode last = null;
        while (curr != null || !stack.isEmpty()) {
            // 一路向左
            if (curr != null) {
                stack.push(curr);
                // 前序：向左前就获取节点
                if (type == TraversalType.PRE) {
                    consumer.accept(curr);
                }
                curr = curr.left;
            } else {
                TreeNode peek = stack.peek();
                // 没有右子树
                if (peek.right == null) {
                    // 中序、后序
                    if (type == TraversalType.IN || type == TraversalType.POST) {
                        consumer.accept(peek);
                    }
                    last = stack.pop();
                } else if (peek.right == last){ // 有右子树，但是走完了
                    // 后序
                    if (type == TraversalType.POST) {
                        consumer.accept(peek);
                    }
                    last = stack.pop();
                } else { // 有右子树，且没有走过
                    // 中序
                    if (type == TraversalType.IN) {
                        consumer.accept(peek);
                    }
                    // 定位到右子树，然后再向左
                    curr = peek.right;
                }
            }
        }
    }
}
