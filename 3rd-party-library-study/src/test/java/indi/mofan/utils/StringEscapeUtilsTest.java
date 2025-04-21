package indi.mofan.utils;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.SneakyThrows;
import org.apache.commons.text.StringEscapeUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

/**
 * @author mofan
 * @date 2023/11/14 14:05
 */
public class StringEscapeUtilsTest implements WithAssertions {

    @Test
    @SneakyThrows
    public void test() {
        var map = new HashMap<String, Object>();
        map.put("data", null);
        JsonMapper mapper = JsonMapper.builder().build();
        String json = mapper.writeValueAsString(map);
        assertThat(json).isEqualTo("{\"data\":null}");
        String escapedJson = StringEscapeUtils.escapeJson(json);
        assertThat(escapedJson).isEqualTo("{\\\"data\\\":null}");
    }

}
