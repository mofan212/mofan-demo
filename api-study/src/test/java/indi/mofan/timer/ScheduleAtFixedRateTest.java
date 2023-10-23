package indi.mofan.timer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author mofan
 * @date 2023/10/23 14:37
 */
public class ScheduleAtFixedRateTest {
    static class Example {
        private final Timer timer;

        public Example(Timer timer) {
            this.timer = timer;
        }

        public void start() {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    System.out.println("executed at: " + System.currentTimeMillis());
                }
            };

            // delay: 延迟时间，单位毫秒
            // period: 执行周期，单位毫秒
            // 任务在延迟时间后执行，每个周期执行一次
            this.timer.scheduleAtFixedRate(task, 1000, 2000);
        }

        public void stop() {
            this.timer.cancel();
        }
    }

    @Test
    public void testScheduleAtFixedRate() {
        Timer timer = Mockito.mock(Timer.class);
        Mockito.doNothing().when(timer).scheduleAtFixedRate(Mockito.any(TimerTask.class), Mockito.anyLong(), Mockito.anyLong());
        Example example = new Example(timer);

        example.start();
        // 休眠两秒
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            Assertions.fail();
        }
        Mockito.verify(timer, Mockito.times(1))
                .scheduleAtFixedRate(Mockito.any(TimerTask.class), Mockito.eq(1000L), Mockito.eq(2000L));

        example.stop();
        Mockito.verify(timer, Mockito.times(1)).cancel();
    }
}
