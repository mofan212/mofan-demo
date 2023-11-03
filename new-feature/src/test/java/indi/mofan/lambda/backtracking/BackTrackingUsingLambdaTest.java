package indi.mofan.lambda.backtracking;

import indi.mofan.lambda.backtracking.node.NodeInfo;
import indi.mofan.lambda.backtracking.node.NodeType;
import indi.mofan.lambda.backtracking.node.container.ContainerNode;
import indi.mofan.lambda.backtracking.node.container.VirtualContainerNode;
import indi.mofan.lambda.backtracking.node.single.SingleA;
import indi.mofan.lambda.backtracking.node.single.SingleB;
import indi.mofan.lambda.backtracking.node.single.SingleC;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author mofan
 * @date 2023/10/18 11:17
 */
public class BackTrackingUsingLambdaTest implements WithAssertions {

    private List<NodeInfo> buildNodes() {
        SingleA a1 = new SingleA("A1");
        SingleA a2 = new SingleA("A2");
        SingleA a3 = new SingleA("A3");
        SingleA a4 = new SingleA("A4");

        SingleB b1 = new SingleB("B1");
        SingleB b2 = new SingleB("B2");

        SingleC c1 = new SingleC("C1");
        SingleC c2 = new SingleC("C2");

        VirtualContainerNode containerA = new VirtualContainerNode("ContainerA", List.of(a1, b1));
        VirtualContainerNode containerB = new VirtualContainerNode("ContainerB", List.of(a2, b2, c1));

        ContainerNode parentContainer = new ContainerNode("parentContainer", List.of(containerA, containerB));

        return List.of(a3, a4, parentContainer, c2);
    }

    @Test
    public void testNodeFindSimpleSupport() {
        NodeFindSimpleSupport support = NodeFindSimpleSupport.from(buildNodes());
        AtomicInteger count = new AtomicInteger(0);
        // 统计节点数量，但不包含 VirtualContainerNode
        support.findNodes(node -> {
            if (!NodeType.VIRTUAL_CONTAINER.equals(node.getNodeType())) {
                count.incrementAndGet();
            }
        });
        assertThat(count).hasValue(9);
    }
}
