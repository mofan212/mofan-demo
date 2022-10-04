package indi.mofan.io.core.filter;

import indi.mofan.io.core.Output;
import indi.mofan.io.core.Receiver;
import indi.mofan.io.core.Sender;


/**
 * @author mofan
 * @date 2022/10/3 18:09
 */
public class Filters {
    static class SpecificationOutputWrapper<T, ReceiverThrowableType extends Throwable>
            implements Output<T, ReceiverThrowableType> {

        final Output<T, ReceiverThrowableType> output;
        final Specification<T> specification;

        public SpecificationOutputWrapper(Output<T, ReceiverThrowableType> output, Specification<T> specification) {
            this.output = output;
            this.specification = specification;
        }

        @Override
        public <SenderThrowableType extends Throwable> void receiveFrom(Sender<T, SenderThrowableType> sender)
                throws ReceiverThrowableType, SenderThrowableType {
            output.receiveFrom(new SpecificationSenderWrapper<>(sender, specification));
        }
    }

    static class SpecificationSenderWrapper<T, SenderThrowableType extends Throwable>
            implements Sender<T, SenderThrowableType> {

        final Sender<T, SenderThrowableType> sender;
        final Specification<T> specification;

        public SpecificationSenderWrapper(Sender<T, SenderThrowableType> sender, Specification<T> specification) {
            this.sender = sender;
            this.specification = specification;
        }

        @Override
        public <ReceiverThrowableType extends Throwable> void sendTo(Receiver<T, ReceiverThrowableType> receiver)
                throws ReceiverThrowableType, SenderThrowableType {
            sender.sendTo(new SpecificationReceiverWrapper<>(receiver, specification));
        }
    }

    static class SpecificationReceiverWrapper<T, ReceiverThrowableType extends Throwable>
            implements Receiver<T, ReceiverThrowableType> {

        final Receiver<T, ReceiverThrowableType> receiver;
        final Specification<T> specification;

        public SpecificationReceiverWrapper(Receiver<T, ReceiverThrowableType> receiver, Specification<T> specification) {
            this.receiver = receiver;
            this.specification = specification;
        }

        @Override
        public void receive(T item) throws ReceiverThrowableType {
            if(specification.test(item)) {
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


    static class FunctionOutputWrapper<From, To, ReceiverThrowableType extends Throwable>
            implements Output<From, ReceiverThrowableType> {

        final Output<To, ReceiverThrowableType> output;
        final Function<From, To> function;

        public FunctionOutputWrapper(Output<To, ReceiverThrowableType> output, Function<From, To> function) {
            this.output = output;
            this.function = function;
        }

        @Override
        public <SenderThrowableType extends Throwable> void receiveFrom(Sender<From, SenderThrowableType> sender)
                throws ReceiverThrowableType, SenderThrowableType {
            output.receiveFrom(new FunctionSenderWrapper<>(sender, function));
        }
    }

    static class FunctionSenderWrapper<From, To, SenderThrowableType extends Throwable> implements Sender<To, SenderThrowableType> {
        final Sender<From, SenderThrowableType> sender;
        final Function<From, To> function;

        public FunctionSenderWrapper(Sender<From, SenderThrowableType> sender, Function<From, To> function) {
            this.sender = sender;
            this.function = function;
        }

        @Override
        public <ReceiverThrowableType extends Throwable> void sendTo(Receiver<To, ReceiverThrowableType> receiver)
                throws ReceiverThrowableType, SenderThrowableType {
            sender.sendTo(new FunctionReceiverWrapper<>(receiver, function));
        }
    }

    static class FunctionReceiverWrapper<From, To, ReceiverThrowableType extends Throwable>
            implements Receiver<From, ReceiverThrowableType> {

        final Receiver<To, ReceiverThrowableType> receiver;
        final Function<From, To> function;

        public FunctionReceiverWrapper(Receiver<To, ReceiverThrowableType> receiver, Function<From, To> function) {
            this.receiver = receiver;
            this.function = function;
        }

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
