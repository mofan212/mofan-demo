package indi.mofan;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Period;

/**
 * @author mofan
 * @date 2023/3/18 19:51
 */
public class DateTimeIntervalTest {
    @Test
    public void testDateInterval() {
        LocalDate firstDay = LocalDate.of(2023, 1, 1);
        LocalDate now = LocalDate.of(2023, 3, 18);

        Period between = Period.between(firstDay, now);
        Assertions.assertEquals(17, between.getDays());
        Assertions.assertEquals(2, between.getMonths());
        Assertions.assertEquals(76, now.toEpochDay() - firstDay.toEpochDay());
    }
}
