package indi.mofan;


import indi.mofan.trie.Node;
import indi.mofan.trie.TrieNode;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author mofan
 * @date 2025/12/11 15:37
 */
public class Paths2ForestTest implements WithAssertions {

    private Collection<TrieNode> buildForest(List<List<String>> paths) {
        if (CollectionUtils.isEmpty(paths)) {
            return new ArrayList<>();
        }
        TrieNode virtualRoot = new TrieNode();
        for (List<String> path : paths) {
            List<Node> list = path.stream().map(Node::new).toList();
            insertPath(virtualRoot, list);
        }
        return virtualRoot.children().values();
    }

    private void insertPath(TrieNode trieRoot, List<Node> path) {
        TrieNode cur = trieRoot;
        for (Node node : path) {
            String nodeId = node.id();
            if (!cur.children().containsKey(nodeId)) {
                TrieNode trieNode = new TrieNode(nodeId);
                cur.children().put(nodeId, trieNode);
            }
            cur = cur.children().get(nodeId);
        }
    }

    @Test
    public void testBuildForest() {
        var paths = List.of(
                List.of("C"),
                List.of("C", "G"),
                List.of("C", "H", "M"),
                List.of("C", "H", "N"),
                List.of("B", "F")
        );
        Collection<TrieNode> forest = buildForest(paths);
        assertThat(forest).hasSize(2)
                .containsExactlyInAnyOrder(
                        new TrieNode("B", List.of("F")),
                        new TrieNode(
                                "C",
                                Map.of(
                                        "H", new TrieNode("H", List.of("M", "N")),
                                        "G", new TrieNode("G")
                                )
                        )
                );
    }
}
