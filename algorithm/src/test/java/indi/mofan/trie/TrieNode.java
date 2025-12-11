package indi.mofan.trie;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author cheny
 * @date 2025/12/11 16:02
 */
public record TrieNode(String nodeId, Map<String, TrieNode> children) {
    public TrieNode() {
        this(null);
    }

    public TrieNode(String nodeId) {
        this(nodeId, new HashMap<>());
    }

    public TrieNode(String nodeId, Collection<String> childrenIds) {
        this(nodeId, nodeIds2Children(childrenIds));
    }

    public static Map<String, TrieNode> nodeIds2Children(Collection<String> childrenIds) {
        return childrenIds.stream()
                .collect(Collectors.toMap(Function.identity(), TrieNode::new));
    }
}
