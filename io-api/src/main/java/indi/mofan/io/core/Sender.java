package indi.mofan.io.core;

/**
 * @author mofan
 * @date 2022/10/3 18:08
 */
public interface Sender<T, SenderThrowableType extends Throwable> {

    <ReceiverThrowableType extends Throwable> void sendTo(Receiver<T, ReceiverThrowableType> receiver)
            throws ReceiverThrowableType, SenderThrowableType;
}