package indi.mofan.strategy.duck;

import indi.mofan.strategy.duck.enums.DuckType;
import org.springframework.stereotype.Component;

/**
 * @author mofan
 * @date 2022/10/6 15:08
 */
@Component
public class WildDuck extends Duck {
    @Override
    public DuckType getDuckType() {
        return DuckType.WILD;
    }

    public WildDuck() {
        flyBehavior = () -> {
            System.out.println("野鸭能飞");
        };

        swimBehavior = () -> {
            System.out.println("野鸭能游泳");
        };

        quackBehavior = () -> {
            System.out.println("野鸭能叫");
        };
    }
}
