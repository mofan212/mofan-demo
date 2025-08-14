package indi.mofan.apply.prime.delay;


/**
 * @author mofan
 * @date 2025/3/11 17:07
 */
public class MyLinkedList<T> implements MyList<T> {

    private final T head;
    private final MyList<T> tail;

    private MyLinkedList(T head, MyLinkedList<T> tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public T head() {
        return head;
    }

    @Override
    public MyList<T> tail() {
        return tail;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
