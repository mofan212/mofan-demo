package indi.mofan;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author mofan
 * @date 2023/2/17 23:29
 */
public class GetGenericInfoTest {

    static class MyList extends ArrayList<String> {
    }

    static class MyLinkList<T> extends LinkedList<T> {
    }

    interface MyInterface<T> {
    }

    static class MyInterfaceImpl_1 implements MyInterface<String> {
    }

    static class MyInterfaceImpl_2<T> implements MyInterface<T> {
    }

    static class MyClass<T> {
    }

    static class SimpleClass extends MyClass<String> {
    }

    @Test
    public void testGetGenericSuperclass() {
        Type genericSuperclass = MyList.class.getGenericSuperclass();
        Assertions.assertEquals("java.util.ArrayList<java.lang.String>", genericSuperclass.toString());
        Assertions.assertTrue(genericSuperclass instanceof ParameterizedType);
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        Assertions.assertArrayEquals(new Type[]{String.class}, parameterizedType.getActualTypeArguments());
        Assertions.assertEquals(ArrayList.class, parameterizedType.getRawType());
        Assertions.assertNull(parameterizedType.getOwnerType());
        Assertions.assertEquals("java.util.ArrayList<java.lang.String>", parameterizedType.getTypeName());

        genericSuperclass = MyLinkList.class.getGenericSuperclass();
        Assertions.assertEquals("java.util.LinkedList<T>", genericSuperclass.toString());
        Assertions.assertTrue(genericSuperclass instanceof ParameterizedType);
        parameterizedType = (ParameterizedType) genericSuperclass;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Assertions.assertEquals(1, actualTypeArguments.length);
        Assertions.assertEquals("T", actualTypeArguments[0].toString());
        Assertions.assertEquals(LinkedList.class, parameterizedType.getRawType());
        Assertions.assertNull(parameterizedType.getOwnerType());
        Assertions.assertEquals("java.util.LinkedList<T>", parameterizedType.getTypeName());
    }

    @Test
    public void testGetGenericSuperclass_2() {
        Type genericSuperclass = MyInterface.class.getGenericSuperclass();
        Assertions.assertNull(genericSuperclass);

        genericSuperclass = MyInterfaceImpl_1.class.getGenericSuperclass();
        Assertions.assertFalse(genericSuperclass instanceof ParameterizedType);

        genericSuperclass = MyInterfaceImpl_2.class.getGenericSuperclass();
        Assertions.assertFalse(genericSuperclass instanceof ParameterizedType);
    }

    @Test
    public void testGetGenericInterfaces() {
        Type[] genericInterfaces = MyInterface.class.getGenericInterfaces();
        Assertions.assertEquals(0, genericInterfaces.length);

        genericInterfaces = MyInterfaceImpl_1.class.getGenericInterfaces();
        Assertions.assertEquals(1, genericInterfaces.length);
        for (Type genericInterface : genericInterfaces) {
            Assertions.assertTrue(genericInterface instanceof ParameterizedType);
            ParameterizedType type = (ParameterizedType) genericInterface;
            Assertions.assertArrayEquals(new Type[]{String.class}, type.getActualTypeArguments());
            Assertions.assertEquals(MyInterface.class, type.getRawType());
            Assertions.assertEquals(this.getClass(), type.getOwnerType());
        }

        genericInterfaces = MyInterfaceImpl_2.class.getGenericInterfaces();
        Assertions.assertEquals(1, genericInterfaces.length);
        for (Type genericInterface : genericInterfaces) {
            Assertions.assertTrue(genericInterface instanceof ParameterizedType);
            ParameterizedType type = (ParameterizedType) genericInterface;
            Assertions.assertEquals("T", type.getActualTypeArguments()[0].toString());
            Assertions.assertEquals(MyInterface.class, type.getRawType());
            Assertions.assertEquals(this.getClass(), type.getOwnerType());
        }
    }

    @Test
    public void testGetGenericInterfaces_2() {
        Type[] genericInterfaces = MyClass.class.getGenericInterfaces();
        Assertions.assertEquals(0, genericInterfaces.length);

        genericInterfaces = SimpleClass.class.getGenericInterfaces();
        Assertions.assertEquals(0, genericInterfaces.length);
    }
}
