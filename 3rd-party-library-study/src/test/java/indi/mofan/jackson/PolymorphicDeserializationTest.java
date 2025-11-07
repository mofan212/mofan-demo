package indi.mofan.jackson;


import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author mofan
 * @date 2025/11/7 15:33
 */
public class PolymorphicDeserializationTest implements WithAssertions {
    private interface Node {
        String getName();

        void setName(String name);

        String getType();
    }

    private interface ActionNode extends Node {
        String getActionType();
    }

    @EqualsAndHashCode
    private static abstract class AbstractNode implements Node {
        @Getter
        @Setter
        protected String name;

        @Getter
        private String type;
    }

    @NoArgsConstructor
    private static class OneNode extends AbstractNode {

        public OneNode(String name) {
            super();
            this.name = name;
        }

        @Override
        public String getType() {
            return "ONE";
        }
    }

    @NoArgsConstructor
    private static class TwoNode extends AbstractNode {

        public TwoNode(String name) {
            super();
            this.name = name;
        }

        @Override
        public String getType() {
            return "TWO";
        }
    }

    @NoArgsConstructor
    private static class ThreeNode extends AbstractNode {

        @Getter
        @Setter
        private List<? extends Node> nodes;

        public ThreeNode(String name, List<? extends Node> nodes) {
            super();
            this.name = name;
            this.nodes = nodes;
        }

        @Override
        public String getType() {
            return "THREE";
        }
    }

    private static abstract class AbstractActionNode extends AbstractNode implements ActionNode {

        @Getter
        private String actionType;

        @Override
        public String getType() {
            return "ACTION";
        }
    }

    @NoArgsConstructor
    private static class OneActionNode extends AbstractActionNode {

        public OneActionNode(String name) {
            super();
            this.name = name;
        }

        @Override
        public String getActionType() {
            return "ONE-ACTION";
        }
    }

    @NoArgsConstructor
    private static class TwoActionNode extends AbstractActionNode {

        public TwoActionNode(String name) {
            super();
            this.name = name;
        }

        @Override
        public String getActionType() {
            return "TWO-ACTION";
        }
    }

    private static class NodeDeserializer extends JsonDeserializer<Node> {
        @Override
        public Node deserialize(JsonParser parser, DeserializationContext context) throws IOException, JacksonException {
            ObjectMapper mapper = (ObjectMapper) parser.getCodec();
            JsonNode node = mapper.readTree(parser);

            String type = node.get("type").asText();
            Class<? extends Node> nodeClazz = switch (type) {
                case "ONE" -> OneNode.class;
                case "TWO" -> TwoNode.class;
                case "THREE" -> ThreeNode.class;
                case "ACTION" -> ActionNode.class;
                default -> null;
            };
            if (nodeClazz == null) {
                return null;
            }
            if (!ActionNode.class.isAssignableFrom(nodeClazz)) {
                return mapper.treeToValue(node, nodeClazz);
            }
            String actionType = node.get("actionType").asText();
            Class<? extends ActionNode> actionNodeClazz = switch (actionType) {
                case "ONE-ACTION" -> OneActionNode.class;
                case "TWO-ACTION" -> TwoActionNode.class;
                default -> null;
            };
            if (actionNodeClazz == null) {
                return null;
            }
            return mapper.treeToValue(node, actionNodeClazz);
        }
    }

    @Getter
    @Setter
    private static class NodeContainer {
        private String name;

        private List<? extends Node> nodes;
    }

    @Test
    @SneakyThrows
    public void test() {
        // language=json
        String json = """
                {
                  "name": "my-container",
                  "nodes": [
                    {
                      "name": "one",
                      "type": "ONE"
                    },
                    {
                      "name": "two",
                      "type": "TWO"
                    },
                    {
                      "name": "three",
                      "type": "THREE",
                      "nodes": [
                        {
                          "name": "four",
                          "type": "ACTION",
                          "actionType": "ONE-ACTION"
                        }
                      ]
                    },
                    {
                      "name": "five",
                      "type": "ACTION",
                      "actionType": "TWO-ACTION"
                    }
                  ]
                }
                """;
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Node.class, new NodeDeserializer());
        mapper.registerModule(module);

        NodeContainer container = mapper.readValue(json, NodeContainer.class);
        assertThat(container).isNotNull()
                .extracting(NodeContainer::getName, NodeContainer::getNodes)
                .containsExactly(
                        "my-container",
                        List.of(new OneNode("one"),
                                new TwoNode("two"),
                                new ThreeNode("three", List.of(new OneActionNode("four"))),
                                new TwoActionNode("five"))
                );
    }

}
