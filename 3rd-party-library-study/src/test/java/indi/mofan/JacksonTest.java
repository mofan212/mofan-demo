package indi.mofan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import indi.mofan.pojo.Chain;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

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
            Assert.assertNotNull(json);
        } catch (JsonProcessingException e) {
            Assert.fail();
        }
    }
}
