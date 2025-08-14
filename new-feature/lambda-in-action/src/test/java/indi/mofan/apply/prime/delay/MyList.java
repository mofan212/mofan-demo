package indi.mofan.apply.prime.delay;


import java.util.function.Predicate;

/**
 * @author mofan
 * @date 2025/3/11 16:56
 */
public interface MyList<T> {
    T head();

    MyList<T> tail();

    default boolean isEmpty() {
        return true;
    }

    default MyList<T> filter(Predicate<? super T> predicate) {
        return this;
    }
}
