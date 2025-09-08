package indi.mofan.utils;


import org.apache.commons.text.StringSubstitutor;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mofan
 * @date 2025/9/8 15:51
 */
public class StringSubstitutorTest implements WithAssertions {
    @Test
    public void testStringSubstitutor() {
        String input = "GO! ${A}, ${B}, ${A}.";
        Map<String, String> replacements = new HashMap<>();
        replacements.put("A", "1");
        replacements.put("B", "2");

        StringSubstitutor substitutor = new StringSubstitutor(replacements);
        String output = substitutor.replace(input);
        assertThat(output).isEqualTo("GO! 1, 2, 1.");
    }
}
