package indi.mofan;


import indi.mofan.handler.Handler;

/**
 * @author mofan
 * @date 2024/3/7 23:19
 */
public class Pipeline<I, O> {
    private final Handler<I, O> currentHandler;

    Pipeline(Handler<I, O> currentHandler) {
        this.currentHandler = currentHandler;
    }

    <K> Pipeline<I, K> addHandler(Handler<O, K> newHandler) {
        return new Pipeline<>(input -> newHandler.process(currentHandler.process(input)));
    }

    O execute(I input) {
        return currentHandler.process(input);
    }
}
