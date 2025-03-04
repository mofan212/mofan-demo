package indi.mofan;


import java.util.function.Function;

/**
 * @author mofan
 * @date 2024/3/7 23:19
 */
public class Pipeline<I, O> {
    private final Function<I, O> curTask;

    Pipeline(Function<I, O> curTask) {
        this.curTask = curTask;
    }

    <K> Pipeline<I, K> addTask(Function<O, K> newTask) {
        return new Pipeline<>(this.curTask.andThen(newTask));
    }

    O execute(I input) {
        return curTask.apply(input);
    }
}
