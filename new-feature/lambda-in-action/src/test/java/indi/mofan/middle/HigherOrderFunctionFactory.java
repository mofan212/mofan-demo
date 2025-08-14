package indi.mofan.middle;


import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author mofan
 * @date 2025/3/9 15:45
 */
@Component
public class HigherOrderFunctionFactory {
    private static final Map<Type, Function<String, String>> DISPATCHER = new HashMap<>();

    public enum Type {
        ONE, TWO, THREE;
    }

    @PostConstruct
    public void init() {
        DISPATCHER.put(Type.ONE, String::toUpperCase);
        DISPATCHER.put(Type.TWO, String::toLowerCase);
        DISPATCHER.put(Type.THREE, String::trim);
    }

    public String execute(Type type, String value) {
        Function<String, String> dispatcher = DISPATCHER.get(type);
        return dispatcher.apply(value);
    }

    public static Function<String, String> run(Type type) {
        switch (type) {
            case ONE -> {
                return String::toUpperCase;
            }
            case TWO -> {
                return String::toLowerCase;
            }
            case THREE -> {
                return String::trim;
            }
            case null -> throw new RuntimeException("type is null");
        }
    }
}
