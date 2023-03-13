package indi.mofan.observer;

import com.google.common.eventbus.Subscribe;

/**
 * @author mofan
 * @date 2023/3/13 18:24
 */
@EventHandler
public class OrderListener {

    @Subscribe
    public void created(OrderCreateEvent orderCreateEvent) {
        System.out.println(orderCreateEvent);
    }
}
