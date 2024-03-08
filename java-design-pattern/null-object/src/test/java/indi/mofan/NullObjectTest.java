package indi.mofan;

import org.junit.jupiter.api.Test;

/**
 * @author mofan
 * @date 2024/3/8 22:48
 */
public class NullObjectTest {
    @Test
    public void testTraversal() {
        var root = new NodeImpl("1",
                new NodeImpl("11",
                        new NodeImpl("111", NullNode.getInstance(), NullNode.getInstance()),
                        NullNode.getInstance()
                ),
                new NodeImpl("12",
                        NullNode.getInstance(),
                        new NodeImpl("122", NullNode.getInstance(), NullNode.getInstance())
                )
        );
        root.walk();
    }
}
