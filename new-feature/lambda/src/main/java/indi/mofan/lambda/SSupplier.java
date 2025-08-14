package indi.mofan.lambda;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * @author mofan
 * @date 2022/6/7 17:37
 */
public interface SSupplier<T> extends Supplier<T>, Serializable {
}
