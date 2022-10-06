package indi.mofan.strategy.duck;

import indi.mofan.strategy.behavior.FlyBehavior;
import indi.mofan.strategy.behavior.QuackBehavior;
import indi.mofan.strategy.behavior.SwimBehavior;
import indi.mofan.strategy.duck.enums.DuckType;

/**
 * @author mofan
 * @date 2022/10/6 15:02
 */
public abstract class Duck {
    protected FlyBehavior flyBehavior;
    protected QuackBehavior quackBehavior;
    protected SwimBehavior swimBehavior;

    /**
     * 获取鸭子的类型
     *
     * @return 鸭子类型
     */
    public abstract DuckType getDuckType();

    public void fly() {
        if (flyBehavior != null) {
            flyBehavior.fly();
        }
    }

    public void quack() {
        if (quackBehavior != null) {
            quackBehavior.quack();
        }
    }

    public void swim() {
        if (swimBehavior != null) {
            swimBehavior.swim();
        }
    }
}
