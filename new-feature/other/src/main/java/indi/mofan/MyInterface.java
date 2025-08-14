package indi.mofan;

/**
 * @author mofan
 * @date 2023/6/11 21:12
 */
public interface MyInterface {
    private void func() {

    }

    default void defaultFunc() {
        func();
    }

    private static void staticPrivateFunc() {

    }

    static void staticFunc() {
        staticPrivateFunc();
    }
}
