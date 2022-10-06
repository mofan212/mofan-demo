package indi.mofan.chain.handler;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author mofan
 * @date 2022/10/6 16:07
 */
@Order(1)
@Component
public class ConcreteHandlerOne extends Handler {

    public static final String ONE = "one";

    @Override
    public String handleRequest(String request) {
        return ONE.equals(request) ? ONE : "";
    }
}
