package indi.mofan.lambda;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author mofan
 * @date 2022/6/7 17:07
 */
public interface SFunction<T, R> extends Serializable, Function<T, R> {
}
