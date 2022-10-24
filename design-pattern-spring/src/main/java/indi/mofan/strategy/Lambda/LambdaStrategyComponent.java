package indi.mofan.strategy.Lambda;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author mofan
 * @date 2022/10/24 21:43
 */
@Component
public class LambdaStrategyComponent {

    private static final Map<String, Function<String, String>> SERVICE_DISPATCHER = new HashMap<>();

    public static final String CONSTANT = "执行业务: ";

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
