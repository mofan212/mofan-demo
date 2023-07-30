package indi.mofan.io.core.filter;

import indi.mofan.io.core.Output;
import indi.mofan.io.core.Receiver;
import indi.mofan.io.core.Sender;


/**
 * @author mofan
 * @date 2022/10/3 18:09
 */
public class Filters {
    record SpecificationOutputWrapper<T, ReceiverThrowableType extends Throwable>(
            Output<T, ReceiverThrowableType> output, Specification<T> specification)
                implements Output<T, ReceiverThrowableType> {

        @Override
            public <SenderThrowableType extends Throwable> void receiveFrom(Sender<T, SenderThrowableType> sender)
                    throws ReceiverThrowableType, SenderThrowableType {
                output.receiveFrom(new SpecificationSenderWrapper<>(sender, specification));
            }
        }

    record SpecificationSenderWrapper<T, SenderThrowableType extends Throwable>(Sender<T, SenderThrowableType> sender,
                                                                                Specification<T> specification)
                implements Sender<T, SenderThrowableType> {

        @Override
            public <ReceiverThrowableType extends Throwable> void sendTo(Receiver<T, ReceiverThrowableType> receiver)
                    throws ReceiverThrowableType, SenderThrowableType {
                sender.sendTo(new SpecificationReceiverWrapper<>(receiver, specification));
            }
        }

    record SpecificationReceiverWrapper<T, ReceiverThrowableType extends Throwable>(
            Receiver<T, ReceiverThrowableType> receiver, Specification<T> specification)
                implements Receiver<T, ReceiverThrowableType> {

        @Override
            public void receive(T item) throws ReceiverThrowableType {
                if (specification.test(item)) {
                    receiver.receive(item);
                }
            }

            @Override
            public void finished() throws ReceiverThrowableType {
                receiver.finished();
            }
        }

    public static <T, ReceiverThrowableType extends Throwable>
    Output<T, ReceiverThrowableType> filter(Specification<T> specification, final Output<T, ReceiverThrowableType> output) {
        return new SpecificationOutputWrapper<>(output, specification);
    }


    record FunctionOutputWrapper<From, To, ReceiverThrowableType extends Throwable>(
            Output<To, ReceiverThrowableType> output, Function<From, To> function)
                implements Output<From, ReceiverThrowableType> {

        @Override
            public <SenderThrowableType extends Throwable> void receiveFrom(Sender<From, SenderThrowableType> sender)
                    throws ReceiverThrowableType, SenderThrowableType {
                output.receiveFrom(new FunctionSenderWrapper<>(sender, function));
            }
        }

    record FunctionSenderWrapper<From, To, SenderThrowableType extends Throwable>(
            Sender<From, SenderThrowableType> sender,
            Function<From, To> function) implements Sender<To, SenderThrowableType> {

        @Override
            public <ReceiverThrowableType extends Throwable> void sendTo(Receiver<To, ReceiverThrowableType> receiver)
                    throws ReceiverThrowableType, SenderThrowableType {
                sender.sendTo(new FunctionReceiverWrapper<>(receiver, function));
            }
        }

    record FunctionReceiverWrapper<From, To, ReceiverThrowableType extends Throwable>(
            Receiver<To, ReceiverThrowableType> receiver, Function<From, To> function)
                implements Receiver<From, ReceiverThrowableType> {

        @Override
            public void receive(From item) throws ReceiverThrowableType {
                receiver.receive(function.map(item));
            }

            @Override
            public void finished() throws ReceiverThrowableType {
                receiver.finished();
            }
        }

    public static <From, To, ReceiverThrowableType extends Throwable>
    Output<From, ReceiverThrowableType> filter(Function<From, To> function, final Output<To, ReceiverThrowableType> output) {
        return new FunctionOutputWrapper<>(output, function);
    }

    private Filters() {}
}
