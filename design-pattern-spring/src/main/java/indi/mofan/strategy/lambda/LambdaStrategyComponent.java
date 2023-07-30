package indi.mofan.strategy.lambda;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static indi.mofan.strategy.StrategyConstants.CONSTANT;

/**
 * @author mofan
 * @date 2022/10/24 21:43
 */
@Component
public class LambdaStrategyComponent {

    private static final Map<String, Function<String, String>> SERVICE_DISPATCHER = new HashMap<>();

    @PostConstruct
    public void initDispatcher() {
        SERVICE_DISPATCHER.put("1", param -> CONSTANT + param);
        SERVICE_DISPATCHER.put("2", param -> CONSTANT + param);
        SERVICE_DISPATCHER.put("3", param -> CONSTANT + param);
    }

    public String execute(String type, String param) {
        Function<String, String> function = SERVICE_DISPATCHER.get(type);
        return function != null ? function.apply(param) : null;
    }
}
