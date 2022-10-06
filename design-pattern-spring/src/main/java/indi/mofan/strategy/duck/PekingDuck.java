package indi.mofan.strategy.duck;

import indi.mofan.strategy.duck.enums.DuckType;
import org.springframework.stereotype.Component;

/**
 * @author mofan
 * @date 2022/10/6 15:12
 */
@Component
public class PekingDuck extends Duck {
    @Override
    public DuckType getDuckType() {
        return DuckType.PEKING;
    }

    public PekingDuck() {
        flyBehavior = () -> {
            throw new UnsupportedOperationException();
        };

        swimBehavior = () -> {
            throw new UnsupportedOperationException();
        };

        quackBehavior = () -> {
            throw new UnsupportedOperationException();
        };
    }
}
