package indi.mofan.jeps;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

/**
 * @author mofan
 * @date 2023/11/19 23:07
 * @link <a href="https://openjdk.org/jeps/444">JEP 444: Virtual Threads</a>
 */
public class Jep444Test implements WithAssertions {
    @Test
    public void testCreate() {
        Thread duke = Thread.ofVirtual().name("duke").unstarted(() -> System.out.println("Virtual Thread"));
        duke.start();
        assertThat(duke.isVirtual()).isTrue();
    }
}
