package indi.mofan.strategy;

import indi.mofan.strategy.duck.Duck;
import indi.mofan.strategy.duck.enums.DuckType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mofan
 * @date 2022/10/6 15:14
 */
@Component
public class StrategyDuckService implements ApplicationContextAware {

    private final Map<DuckType, Duck> duckStrategyMap = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Duck> duckBeanMap = applicationContext.getBeansOfType(Duck.class);
        duckBeanMap.values().forEach(i -> duckStrategyMap.put(i.getDuckType(), i));
    }

    public void fly(DuckType type) {
        Duck duck = duckStrategyMap.get(type);
        if (duck != null) {
            duck.fly();
        }
    }

    public void quack(DuckType type) {
        Duck duck = duckStrategyMap.get(type);
        if (duck != null) {
            duck.quack();
        }
    }

    public void swim(DuckType type) {
        Duck duck = duckStrategyMap.get(type);
        if (duck != null) {
            duck.swim();
        }
    }
}
