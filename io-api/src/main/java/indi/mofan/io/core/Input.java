package indi.mofan.io.core;

/**
 * @author mofan
 * @date 2022/10/3 18:07
 */
public interface Input<T, SenderThrowableType extends Throwable> {

    <ReceiverThrowableType extends Throwable> void transferTo(Output<T, ReceiverThrowableType> output)
            throws SenderThrowableType, ReceiverThrowableType;
}
