package indi.mofan;

import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertEquals("java.util.ArrayList<java.lang.String>", genericSuperclass.toString());
        Assert.assertTrue(genericSuperclass instanceof ParameterizedType);
        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        Assert.assertArrayEquals(new Type[]{String.class}, parameterizedType.getActualTypeArguments());
        Assert.assertEquals(ArrayList.class, parameterizedType.getRawType());
        Assert.assertNull(parameterizedType.getOwnerType());
        Assert.assertEquals("java.util.ArrayList<java.lang.String>", parameterizedType.getTypeName());

        genericSuperclass = MyLinkList.class.getGenericSuperclass();
        Assert.assertEquals("java.util.LinkedList<T>", genericSuperclass.toString());
        Assert.assertTrue(genericSuperclass instanceof ParameterizedType);
        parameterizedType = (ParameterizedType) genericSuperclass;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Assert.assertEquals(1, actualTypeArguments.length);
        Assert.assertEquals("T", actualTypeArguments[0].toString());
        Assert.assertEquals(LinkedList.class, parameterizedType.getRawType());
        Assert.assertNull(parameterizedType.getOwnerType());
        Assert.assertEquals("java.util.LinkedList<T>", parameterizedType.getTypeName());
    }

    @Test
    public void testGetGenericSuperclass_2() {
        Type genericSuperclass = MyInterface.class.getGenericSuperclass();
        Assert.assertNull(genericSuperclass);

        genericSuperclass = MyInterfaceImpl_1.class.getGenericSuperclass();
        Assert.assertFalse(genericSuperclass instanceof ParameterizedType);

        genericSuperclass = MyInterfaceImpl_2.class.getGenericSuperclass();
        Assert.assertFalse(genericSuperclass instanceof ParameterizedType);
    }

    @Test
    public void testGetGenericInterfaces() {
        Type[] genericInterfaces = MyInterface.class.getGenericInterfaces();
        Assert.assertEquals(0, genericInterfaces.length);

        genericInterfaces = MyInterfaceImpl_1.class.getGenericInterfaces();
        Assert.assertEquals(1, genericInterfaces.length);
        for (Type genericInterface : genericInterfaces) {
            Assert.assertTrue(genericInterface instanceof ParameterizedType);
            ParameterizedType type = (ParameterizedType) genericInterface;
            Assert.assertArrayEquals(new Type[]{String.class}, type.getActualTypeArguments());
            Assert.assertEquals(MyInterface.class, type.getRawType());
            Assert.assertEquals(this.getClass(), type.getOwnerType());
        }

        genericInterfaces = MyInterfaceImpl_2.class.getGenericInterfaces();
        Assert.assertEquals(1, genericInterfaces.length);
        for (Type genericInterface : genericInterfaces) {
            Assert.assertTrue(genericInterface instanceof ParameterizedType);
            ParameterizedType type = (ParameterizedType) genericInterface;
            Assert.assertEquals("T", type.getActualTypeArguments()[0].toString());
            Assert.assertEquals(MyInterface.class, type.getRawType());
            Assert.assertEquals(this.getClass(), type.getOwnerType());
        }
    }

    @Test
    public void testGetGenericInterfaces_2() {
        Type[] genericInterfaces = MyClass.class.getGenericInterfaces();
        Assert.assertEquals(0, genericInterfaces.length);

        genericInterfaces = SimpleClass.class.getGenericInterfaces();
        Assert.assertEquals(0, genericInterfaces.length);
    }
}
