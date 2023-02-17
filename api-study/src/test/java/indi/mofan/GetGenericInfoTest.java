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

    @Test
    public void testGetGenericSuperclass() {
        Class<MyList> myListClazz = MyList.class;
        Type genericSuperclass = myListClazz.getGenericSuperclass();
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
}
