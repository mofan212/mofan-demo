package indi.mofan.lombok;


import com.fasterxml.jackson.databind.json.JsonMapper;
import indi.mofan.lombok.delegate.Delegation;
import indi.mofan.lombok.delegate.DelegationExample;
import lombok.SneakyThrows;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * @author mofan
 * @date 2025/11/25 15:22
 */
public class DelegateTest {

    @Test
    @SneakyThrows
    public void testSerialization() {
        DelegationExample example = new DelegationExample();
        example.setFlag(true);
        example.setInteger(1);
        example.getList().add("A");

        JsonMapper mapper = JsonMapper.builder().build();
        String json = mapper.writeValueAsString(example);
        JsonAssertions.assertThatJson(json)
                .isObject()
                .isNotNull()
                .containsOnly(Map.entry(Delegation.Fields.integer, 1))
                .doesNotContainKeys(Delegation.Fields.flag, Delegation.Fields.list);
    }
}
