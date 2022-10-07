package indi.mofan;

import indi.mofan.chain.ChainOfResponsibility;
import indi.mofan.chain.handler.ConcreteHandlerOne;
import indi.mofan.chain.handler.ConcreteHandlerTwo;
import indi.mofan.config.SpringConfig;
import indi.mofan.singleton.Elvis;
import indi.mofan.singleton.ElvisStealer;
import indi.mofan.strategy.StrategyDuckService;
import indi.mofan.strategy.duck.enums.DuckType;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;

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

    @Test
    public void testReadResolve() {
        byte[] bytes = new byte[]{-84, -19, 0, 5, 115, 114, 0, 33, 105, 110, 100, 105, 46,
                109, 111, 102, 97, 110, 46, 115, 105, 110, 103, 108, 101, 116, 111, 110,
                46, 69, 108, 118, 105, 115, 83, 116, 101, 97, 108, 101, 114, -70, 15, 35,
                30, 42, 78, -75, 29, 2, 0, 1, 76, 0, 7, 112, 97, 121, 108, 111, 97, 100,
                116, 0, 28, 76, 105, 110, 100, 105, 47, 109, 111, 102, 97, 110, 47, 115,
                105, 110, 103, 108, 101, 116, 111, 110, 47, 69, 108, 118, 105, 115, 59,
                120, 112, 115, 114, 0, 26, 105, 110, 100, 105, 46, 109, 111, 102, 97,
                110, 46, 115, 105, 110, 103, 108, 101, 116, 111, 110, 46, 69, 108, 118,
                105, 115, -117, -79, -14, 18, 46, 14, 14, -104, 2, 0, 1, 91, 0, 13, 102,
                97, 118, 111, 114, 105, 116, 101, 83, 111, 110, 103, 115, 116, 0, 19, 91,
                76, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 83, 116, 114, 105, 110,
                103, 59, 120, 112, 117, 114, 0, 19, 91, 76, 106, 97, 118, 97, 46, 108, 97,
                110, 103, 46, 83, 116, 114, 105, 110, 103, 59, -83, -46, 86, -25, -23, 29,
                123, 71, 2, 0, 0, 120, 112, 0, 0, 0, 2, 116, 0, 9, 72, 111, 117, 110, 100,
                32, 68, 111, 103, 116, 0, 16, 72, 101, 97, 114, 116, 98, 114, 101, 97, 107,
                32, 72, 111, 116, 101, 108};

        Elvis elvis = (Elvis) deserialize(bytes);
        Elvis impersonator = ElvisStealer.getImpersonator();

        elvis.printFavorites();
        impersonator.printFavorites();
    }

    @Test
    @SneakyThrows
    public void test() {
        Elvis instance = Elvis.INSTANCE;
        Class<? extends Elvis> clazz = instance.getClass();
        Field field = clazz.getDeclaredField("favoriteSongs");
        field.setAccessible(true);
        System.out.println(instance);
    }

    @SneakyThrows
    public static byte[] getBytesFromObject(Serializable obj) {
        if (obj == null) {
            return null;
        }
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bo);
        oos.writeObject(obj);
        return bo.toByteArray();
    }

    @SneakyThrows
    private static Object deserialize(byte[] sf) {
        InputStream is = new ByteArrayInputStream(sf);
        ObjectInputStream ois = new ObjectInputStream(is);
        return ois.readObject();
    }
}
