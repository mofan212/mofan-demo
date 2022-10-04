package indi.mofan.io.core.filter;

/**
 * @author mofan
 * @date 2022/10/3 18:10
 */
public interface Function<From, To> {
    /**
     * @return return the transformed data. {@code null} to indicate ignore the input data.
     */
    To map(From from);
}
