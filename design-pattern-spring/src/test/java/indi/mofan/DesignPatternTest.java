package indi.mofan;

import indi.mofan.chain.ChainOfResponsibility;
import indi.mofan.chain.handler.ConcreteHandlerOne;
import indi.mofan.chain.handler.ConcreteHandlerTwo;
import indi.mofan.config.SpringConfig;
import indi.mofan.strategy.StrategyDuckService;
import indi.mofan.strategy.duck.enums.DuckType;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    private ChainOfResponsibility chainOfResponsibility;

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

    @Test
    public void testChainOfResponsibility() {
        String one = chainOfResponsibility.exec(ConcreteHandlerOne.ONE);
        Assert.assertEquals(ConcreteHandlerOne.ONE, one);

        String two = chainOfResponsibility.exec(ConcreteHandlerTwo.TWO);
        Assert.assertEquals(ConcreteHandlerTwo.TWO, two);

        String three = chainOfResponsibility.exec("three");
        Assert.assertTrue(StringUtils.isEmpty(three));
    }
}
