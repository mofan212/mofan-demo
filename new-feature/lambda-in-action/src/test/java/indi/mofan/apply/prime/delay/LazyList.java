package indi.mofan.apply.prime.delay;


import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author mofan
 * @date 2025/3/11 16:57
 */
public class LazyList<T> implements MyList<T> {

    private final T head;
    /**
     * 使用 Supplier 获取 tail，延迟加载下一个节点
     */
    private final Supplier<MyList<T>> tail;

    private LazyList(T head, Supplier<MyList<T>> tail) {
        this.head = head;
        this.tail = tail;
    }

    public static LazyList<Integer> from(int n) {
        return new LazyList<>(n, () -> from(n + 1));
    }

    public static MyList<Integer> primes(MyList<Integer> numbers) {
        return new LazyList<>(
                numbers.head(),
                () -> primes(numbers.tail().filter(i -> i % numbers.head() != 0))
        );
    }

    public MyList<T> filter(Predicate<? super T> predicate) {
        if (isEmpty()) {
            return this;
        }
        return predicate.test(head())
                ? new LazyList<>(head(), () -> tail().filter(predicate))
                : tail().filter(predicate);
    }

    @Override
    public T head() {
        return head;
    }

    @Override
    public MyList<T> tail() {
        return tail.get();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
