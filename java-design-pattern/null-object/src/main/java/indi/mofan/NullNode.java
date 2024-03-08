package indi.mofan;

import lombok.Getter;

/**
 * @author mofan
 * @date 2024/3/8 22:46
 */
public final class NullNode implements Node {

    @Getter
    private static final NullNode instance = new NullNode();

    private NullNode() {
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public int getTreeSize() {
        return 0;
    }

    @Override
    public Node left() {
        return null;
    }

    @Override
    public Node right() {
        return null;
    }

    @Override
    public void walk() {
        // do nothing
    }
}
