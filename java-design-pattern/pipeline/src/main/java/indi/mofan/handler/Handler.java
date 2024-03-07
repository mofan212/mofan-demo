package indi.mofan.handler;

/**
 * @author mofan
 * @date 2024/3/7 23:14
 */
public interface Handler<I, O> {
    O process(I input);
}
