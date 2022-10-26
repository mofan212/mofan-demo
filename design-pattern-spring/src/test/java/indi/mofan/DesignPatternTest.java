package indi.mofan;

import indi.mofan.chain.ChainOfResponsibility;
import indi.mofan.chain.handler.ConcreteHandlerOne;
import indi.mofan.chain.handler.ConcreteHandlerTwo;
import indi.mofan.config.SpringConfig;
import indi.mofan.strategy.Lambda.ComplexStrategyEnum;
import indi.mofan.strategy.Lambda.LambdaStrategyComponent;
import indi.mofan.strategy.StrategyDuckService;
import indi.mofan.strategy.StrategyEnum;
import indi.mofan.strategy.duck.enums.DuckType;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static indi.mofan.strategy.StrategyConstants.CONSTANT;

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

    @Autowired
    private LambdaStrategyComponent component;

    @Test
    public void testLambdaStrategy() {
        Assert.assertEquals(CONSTANT + "1", component.execute("1", "1"));
        Assert.assertEquals(CONSTANT + "2", component.execute("2", "2"));
        Assert.assertEquals(CONSTANT + "3", component.execute("3", "3"));
        Assert.assertNull(component.execute("4", "4"));
    }

    @Test
    public void testStrategyEnum() {
        Assert.assertEquals(CONSTANT + "1", StrategyEnum.ONE.doSomething("1"));
        Assert.assertEquals(CONSTANT + "2", StrategyEnum.TWO.doSomething("2"));
        Assert.assertEquals(CONSTANT + "3", StrategyEnum.THREE.doSomething("3"));
    }

    @Test
    public void testComplexStrategyEnum() {
        Assert.assertEquals("ONE", ComplexStrategyEnum.FIRST.toDo());
        Assert.assertEquals("TWO", ComplexStrategyEnum.SECOND.toDo());
        Assert.assertEquals("ONE", ComplexStrategyEnum.THIRD.toDo());
    }
}
