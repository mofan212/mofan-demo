package indi.mofan.io.core;

/**
 * @author mofan
 * @date 2022/10/3 18:08
 */
public interface Receiver<T, ReceiverThrowableType extends Throwable> {
    void receive(T item) throws ReceiverThrowableType;

    void finished() throws ReceiverThrowableType;
}
