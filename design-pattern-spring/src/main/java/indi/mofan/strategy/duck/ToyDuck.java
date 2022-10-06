package indi.mofan.strategy.duck;

import indi.mofan.strategy.duck.enums.DuckType;
import org.springframework.stereotype.Component;

/**
 * @author mofan
 * @date 2022/10/6 15:11
 */
@Component
public class ToyDuck extends Duck {
    @Override
    public DuckType getDuckType() {
        return DuckType.TOY;
    }

    public ToyDuck() {
        flyBehavior = () -> {
            System.out.println("玩具鸭能飞");
        };

        swimBehavior = () -> {
            throw new UnsupportedOperationException();
        };

        quackBehavior = () -> {
            System.out.println("玩具鸭能叫");
        };
    }
}
