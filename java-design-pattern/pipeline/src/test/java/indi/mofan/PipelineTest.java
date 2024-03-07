package indi.mofan;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

/**
 * @author mofan
 * @date 2024/3/7 23:21
 */
public class PipelineTest implements WithAssertions {
    @Test
    public void test() {
        String str = "abc";
        var pipeline = new Pipeline<String, String>(input -> input + "d")
                .addHandler(String::length);
        assertThat(pipeline.execute(str)).isEqualTo(4);
    }
}
