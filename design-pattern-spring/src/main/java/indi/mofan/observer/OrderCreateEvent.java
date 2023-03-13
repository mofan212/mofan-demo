package indi.mofan.observer;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * @author mofan
 * @date 2023/3/13 18:24
 */
@ToString
@AllArgsConstructor
public class OrderCreateEvent {
    private final Long orderId;
}
