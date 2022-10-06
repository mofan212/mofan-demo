package indi.mofan;

import indi.mofan.config.SpringConfig;
import indi.mofan.strategy.StrategyDuckService;
import indi.mofan.strategy.duck.enums.DuckType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author mofan
 * @date 2022/10/6 14:48
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SpringConfig.class)
public class DesignPatternTest {

    @Autowired
    private StrategyDuckService duckService;

    @Test
    public void testStrategy() {
        try {
            duckService.fly(DuckType.PEKING);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof UnsupportedOperationException);
        }

        try {
            duckService.swim(DuckType.TOY);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof UnsupportedOperationException);
        }

        try {
            duckService.swim(DuckType.WILD);
            duckService.fly(DuckType.WILD);
            duckService.quack(DuckType.WILD);
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
