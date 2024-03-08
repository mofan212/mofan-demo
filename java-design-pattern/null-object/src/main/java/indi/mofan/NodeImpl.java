package indi.mofan;

import lombok.extern.slf4j.Slf4j;

/**
 * @author mofan
 * @date 2024/3/8 22:43
 */
@Slf4j
public record NodeImpl(String name, Node left, Node right) implements Node {

    @Override
    public int getTreeSize() {
        return 1 + left.getTreeSize() + right.getTreeSize();
    }

    @Override
    public Node left() {
        return this.left;
    }

    @Override
    public Node right() {
        return this.right;
    }

    @Override
    public void walk() {
        log.info(this.name);
        if (left.getTreeSize() > 0) {
            left.walk();
        }
        if (right.getTreeSize() > 0) {
            right.walk();
        }
    }
}
