package indi.mofan.observer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mofan
 * @date 2023/3/13 18:26
 */
@Service
public class OrderService {
    @Autowired
    private EventBusRegister eventBusRegister;

    public void createOrder() {
        // 省略创建订单的业务，直接发事件
        eventBusRegister.postAsync(new OrderCreateEvent(1L));
    }
}
