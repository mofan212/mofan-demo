package indi.mofan;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

/**
 * @author mofan
 * @date 2023/6/13 20:18
 */
public class SealedClassesTest implements WithAssertions {

    static abstract sealed class Person permits Teacher, Student {

    }

    static abstract sealed class Teacher extends Person {

    }

    static final class HighSchoolTeacher extends Teacher {

    }

    static non-sealed class Student extends Person {

    }

    @Test
    public void testIsSealed() {
        assertThat(HighSchoolTeacher.class.isSealed()).isEqualTo(false);
        assertThat(Teacher.class.isSealed()).isEqualTo(true);
    }
}
