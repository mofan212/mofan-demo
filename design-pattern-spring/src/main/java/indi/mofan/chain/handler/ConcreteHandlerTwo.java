package indi.mofan.chain.handler;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author mofan
 * @date 2022/10/6 16:17
 */
@Order(2)
@Component
public class ConcreteHandlerTwo extends Handler {
    public static final String TWO = "two";

    @Override
    public String handleRequest(String request) {
        return TWO.equals(request) ? TWO : "";
    }
}
