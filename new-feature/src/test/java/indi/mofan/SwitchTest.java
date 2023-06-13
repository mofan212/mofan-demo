package indi.mofan;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

/**
 * @author mofan
 * @date 2023/6/13 11:29
 */
public class SwitchTest implements WithAssertions {
    @Test
    public void testSimplyUse() {
        assertThat(getLevel(98)).isEqualTo("优秀");
        assertThat(getLevel(62)).isEqualTo("及格");
        assertThat(getLevel(57)).isEqualTo("不及格");
    }

    private String getLevel(int score) {
        score /= 10;
        return switch (score) {
            case 10, 9 -> "优秀";
            case 8, 7 -> "良好";
            case 6 -> {
                System.out.println("继续努力");
                yield "及格";
            }
            default -> "不及格";
        };
    }

}
