package indi.mofan.higher;

import indi.mofan.pojo.TreeNode;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mofan
 * @date 2024/7/10 14:14
 */
public class BinaryTreeTraversalTest implements WithAssertions {

    final TreeNode root = new TreeNode(1,
            new TreeNode(2,
                    new TreeNode(4),
                    null
            ),
            new TreeNode(3,
                    new TreeNode(5),
                    new TreeNode(6)
            )
    );

    @Test
    public void testRecursiveTraversal() {
        List<Integer> res = new ArrayList<>();
        TreeNode.recursiveTraversal(root, TreeNode.TraversalType.PRE, i -> res.add(i.getVal()));
        assertThat(res).containsExactly(1, 2, 4, 3, 5, 6);

        res.clear();
        TreeNode.recursiveTraversal(root, TreeNode.TraversalType.IN, i -> res.add(i.getVal()));
        assertThat(res).containsExactly(4, 2, 1, 5, 3, 6);

        res.clear();
        TreeNode.recursiveTraversal(root, TreeNode.TraversalType.POST, i -> res.add(i.getVal()));
        assertThat(res).containsExactly(4, 2, 5, 6, 3, 1);
    }

    @Test
    public void testTraversal() {
        List<Integer> res = new ArrayList<>();
        TreeNode.traverse(root, TreeNode.TraversalType.PRE, i -> res.add(i.getVal()));
        assertThat(res).containsExactly(1, 2, 4, 3, 5, 6);

        res.clear();
        TreeNode.traverse(root, TreeNode.TraversalType.IN, i -> res.add(i.getVal()));
        assertThat(res).containsExactly(4, 2, 1, 5, 3, 6);

        res.clear();
        TreeNode.traverse(root, TreeNode.TraversalType.POST, i -> res.add(i.getVal()));
        assertThat(res).containsExactly(4, 2, 5, 6, 3, 1);
    }
}
