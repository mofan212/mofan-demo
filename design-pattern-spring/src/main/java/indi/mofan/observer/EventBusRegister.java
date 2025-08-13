package indi.mofan.observer;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * @author mofan
 * @date 2023/3/13 18:00
 */
@Component
public class EventBusRegister {

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    /**
     * 管理同步事件
     */
    private final EventBus syncEventBus = new EventBus();

    /**
     * 管理异步事件（测试使用 JDK 默认的线程池即可）
     */
    private final AsyncEventBus asyncEventBus = new AsyncEventBus(Executors.newCachedThreadPool());

    public void postSync(Object event) {
        syncEventBus.post(event);
    }

    public void postAsync(Object event) {
        asyncEventBus.post(event);
    }

    @PostConstruct
    public void init() {
        List<Object> beans =
                new ArrayList<>(beanFactory.getBeansWithAnnotation(EventHandler.class).values());
        beans.forEach(i -> {
            syncEventBus.register(i);
            asyncEventBus.register(i);
        });
    }
}
