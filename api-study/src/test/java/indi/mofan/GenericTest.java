package indi.mofan;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

/**
 * @author mofan
 * @date 2023/3/14 23:19
 */
public class GenericTest {
    static class Obj {
    }

    private <T, U extends Comparable<U>, R> void func(T obj,
                                                      Function<T, U> fun1,
                                                      Function<R, U> fun2) {
    }

    @Test
    public void testCompile() {
        Obj obj = new Obj();
        func(obj, Object::hashCode, Obj::hashCode);
        func(obj, Obj::toString, Object::toString);

//        func(obj, Obj::hashCode, Obj::toString);
    }
}
