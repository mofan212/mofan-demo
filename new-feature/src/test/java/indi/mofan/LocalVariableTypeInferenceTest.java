package indi.mofan;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mofan
 * @date 2023/6/11 18:25
 */
public class LocalVariableTypeInferenceTest implements WithAssertions {
    @Test
    public void testSimplyUse() {
        var list = new ArrayList<String>();
        list.add("hello");
        assertThat(list)
                .isExactlyInstanceOf(ArrayList.class)
                .containsAll(List.of("hello"));


        for (var v : list) {
            assertThat(v).isExactlyInstanceOf(String.class);
        }

        for (var i = 0; i < 10; i++) {
            assertThat(i).isExactlyInstanceOf(Integer.class);
        }

        // 针对复杂的嵌套泛型相当有用
        var map = new HashMap<String, Map<Integer, List<String>>>();
    }
}
