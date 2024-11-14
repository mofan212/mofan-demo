package indi.mofan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import indi.mofan.pojo.Chain;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author mofan
 * @date 2022/11/25 17:52
 */
public class JacksonTest {

    @Test
    @SneakyThrows
    public void testChainCall() {
        Chain chain = Chain.chainFactory().setName("testName").setLength(1.3);

        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.deactivateDefaultTyping().writeValueAsString(chain);
            Assertions.assertNotNull(json);
        } catch (JsonProcessingException e) {
            Assertions.fail();
        }
    }

    private enum MyEnum {
        ONE, TWO, THREE
    }

    @Test
    public void testString2Enum() {
        ObjectMapper mapper = new ObjectMapper();
        MyEnum myEnum = mapper.convertValue("ONE", new TypeReference<>() {
        });
        Assertions.assertEquals(MyEnum.ONE, myEnum);
    }
}
