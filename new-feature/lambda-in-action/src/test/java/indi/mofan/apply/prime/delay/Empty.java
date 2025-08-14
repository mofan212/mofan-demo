package indi.mofan.apply.prime.delay;


/**
 * @author mofan
 * @date 2025/3/11 16:58
 */
public class Empty<T> implements MyList<T> {

    public static <T> Empty<T> empty() {
        return new Empty<>();
    }

    @Override
    public T head() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MyList<T> tail() {
        throw new UnsupportedOperationException();
    }
}
