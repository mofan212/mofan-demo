package indi.mofan;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import static indi.mofan.Dog.showStr;

/**
 * @author mofan
 * @date 2023/6/13 20:03
 */
public class RecordTest implements WithAssertions {
    @Test
    public void test() {
        Dog dog = new Dog("小黑", 2);
        Dog black = new Dog("小黑", 2);
        assertThat(black).isEqualTo(dog);

        Dog yellow = Dog.from("yellow");
        assertThat(yellow.getUpperCaseName()).isEqualTo("YELLOW");

        Dog.str("ABC");
        assertThat(showStr()).isEqualTo("ABC");

        assertThat(Dog.class.isRecord()).isEqualTo(true);
    }
}
