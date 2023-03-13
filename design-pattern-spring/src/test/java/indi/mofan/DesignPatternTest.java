package indi.mofan;

import indi.mofan.chain.ChainOfResponsibility;
import indi.mofan.chain.handler.ConcreteHandlerOne;
import indi.mofan.chain.handler.ConcreteHandlerTwo;
import indi.mofan.config.SpringConfig;
import indi.mofan.observer.OrderService;
import indi.mofan.strategy.Lambda.ComplexStrategyEnum;
import indi.mofan.strategy.Lambda.LambdaStrategyComponent;
import indi.mofan.strategy.StrategyDuckService;
import indi.mofan.strategy.StrategyEnum;
import indi.mofan.strategy.duck.enums.DuckType;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static indi.mofan.strategy.StrategyConstants.CONSTANT;

/**
 * @author mofan
 * @date 2022/10/6 14:48
 */
@ExtendWith(SpringExtension.class)
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
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof UnsupportedOperationException);
        }

        try {
            duckService.swim(DuckType.TOY);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof UnsupportedOperationException);
        }

        try {
            duckService.swim(DuckType.WILD);
            duckService.fly(DuckType.WILD);
            duckService.quack(DuckType.WILD);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void testChainOfResponsibility() {
        String one = chainOfResponsibility.exec(ConcreteHandlerOne.ONE);
        Assertions.assertEquals(ConcreteHandlerOne.ONE, one);

        String two = chainOfResponsibility.exec(ConcreteHandlerTwo.TWO);
        Assertions.assertEquals(ConcreteHandlerTwo.TWO, two);

        String three = chainOfResponsibility.exec("three");
        Assertions.assertTrue(StringUtils.isEmpty(three));
    }

    @Autowired
    private LambdaStrategyComponent component;

    @Test
    public void testLambdaStrategy() {
        Assertions.assertEquals(CONSTANT + "1", component.execute("1", "1"));
        Assertions.assertEquals(CONSTANT + "2", component.execute("2", "2"));
        Assertions.assertEquals(CONSTANT + "3", component.execute("3", "3"));
        Assertions.assertNull(component.execute("4", "4"));
    }

    @Test
    public void testStrategyEnum() {
        Assertions.assertEquals(CONSTANT + "1", StrategyEnum.ONE.doSomething("1"));
        Assertions.assertEquals(CONSTANT + "2", StrategyEnum.TWO.doSomething("2"));
        Assertions.assertEquals(CONSTANT + "3", StrategyEnum.THREE.doSomething("3"));
    }

    @Test
    public void testComplexStrategyEnum() {
        Assertions.assertEquals("ONE", ComplexStrategyEnum.FIRST.toDo());
        Assertions.assertEquals("TWO", ComplexStrategyEnum.SECOND.toDo());
        Assertions.assertEquals("ONE", ComplexStrategyEnum.THIRD.toDo());
    }

    @Autowired
    private OrderService orderService;

    @Test
    public void testEventBus() {
        orderService.createOrder();
    }
}
