package indi.mofan;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Integer.valueOf;

/**
 * @author mofan
 * @date 2023/6/11 18:41
 */
public class InstanceOfTest implements WithAssertions {

    private Object func() {
        int random = ThreadLocalRandom.current().nextInt(10);
        if (random % 2 == 1) {
            return String.valueOf(random);
        } else {
            return random;
        }
    }

    @Test
    public void test() {
        Object obj = func();
        if (obj instanceof Integer result) {
            assertThat(result).isEven();
        }
        if (obj instanceof String str) {
            assertThat(valueOf(str)).isOdd();
        }
    }
}
