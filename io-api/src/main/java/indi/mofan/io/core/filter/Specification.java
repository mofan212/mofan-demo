package indi.mofan.io.core.filter;

/**
 * @author mofan
 * @date 2022/10/3 18:10
 */
public interface Specification<T> {
    boolean test(T item);
}
