package indi.mofan.io.core;

/**
 * @author mofan
 * @date 2022/10/3 18:08
 */
public interface Output<T, ReceiverThrowableType extends Throwable> {

    <SenderThrowableType extends Throwable> void receiveFrom(Sender<T, SenderThrowableType> sender)
            throws ReceiverThrowableType, SenderThrowableType;
}
