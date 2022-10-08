package indi.mofan;

import indi.mofan.pojo.Person;
import indi.mofan.pojo.Student;
import org.junit.Assert;
import org.junit.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author mofan
 * @date 2022/8/16 10:37
 */
public class TestMethodHandle {

    private final MethodHandles.Lookup lookup = MethodHandles.lookup();

    @Test
    public void testConstructor() throws Throwable {
        // 方法的签名
        MethodType noArgs = MethodType.methodType(void.class);
        // 指定类、方法的签名、哪一种方法
        MethodHandle noArgsMethodHandle = lookup.findConstructor(Person.class, noArgs);
        // invoke 反射调用
        Person noArgsPerson = (Person) noArgsMethodHandle.invoke();
        Assert.assertNotNull(noArgsPerson);

        // 第一个参数是返回值类型，后面的是参数列表的数据类型
        MethodType allArgs = MethodType.methodType(void.class, String.class, Integer.class);
        MethodHandle allArgsMethodHandle = lookup.findConstructor(Person.class, allArgs);
        Person allArgsPerson = (Person) allArgsMethodHandle.invoke("test", 18);
        Assert.assertEquals("test", allArgsPerson.getName());
        Assert.assertEquals(18, allArgsPerson.getAge().intValue());
    }

    @Test
    public void testPublicMethod() throws Throwable {
        // getName
        MethodType getNameMethodType = MethodType.methodType(String.class);
        MethodHandle getNameMethodHandle = lookup.findVirtual(Person.class, "getName", getNameMethodType);
        Person perSon = new Person("test", 18);
        String name = (String) getNameMethodHandle.invoke(perSon);
        Assert.assertEquals("test", name);

        // setAge
        MethodType setAgeMethodType = MethodType.methodType(void.class, Integer.class);
        MethodHandle setAgeMethodHandle = lookup.findVirtual(Person.class, "setAge", setAgeMethodType);
        setAgeMethodHandle.invoke(perSon, 100);
        Assert.assertEquals(100, perSon.getAge().intValue());
    }

    @Test
    public void testPrivateMethod() throws Throwable {
        Method getStr = Person.class.getDeclaredMethod("getStr", String.class, Integer.class);
        getStr.setAccessible(true);
        MethodHandle getStrMh = lookup.unreflect(getStr);
        String str = (String) getStrMh.invoke(new Person("test", 18), "TEST", 20);
        Assert.assertEquals("TEST - 20", str);
    }

    @Test
    public void testPublicStaticMethod() throws Throwable {
        MethodType returnIntMethodType = MethodType.methodType(Integer.class, Integer.class);
        MethodHandle returnIntMh = lookup.findStatic(Person.class, "returnInt", returnIntMethodType);
        Integer result = (Integer) returnIntMh.invoke(2);
        Assert.assertEquals(2, result.intValue());
    }

    @Test
    public void testPublicProperties() throws Throwable {
        MethodHandle boolMh = lookup.findGetter(Person.class, "bool", Boolean.class);
        Person perSon = new Person();
        perSon.setBool(true);
        Boolean bool = (Boolean) boolMh.invoke(perSon);
        Assert.assertTrue(bool);
    }

    @Test
    public void testPrivateProperties() throws Throwable {
        Field field = Person.class.getDeclaredField("name");
        field.setAccessible(true);
        MethodHandle nameMh = lookup.unreflectGetter(field);
        String name = (String) nameMh.invoke(new Person("test", 18));
        Assert.assertEquals("test", name);
    }

    @Test
    public void testBindTo() throws Throwable {
        MethodType setNameMethodType = MethodType.methodType(void.class, String.class);
        MethodHandle setNameMh = lookup.findVirtual(Person.class, "setName", setNameMethodType);
        Student student = new Student();
        // bindTo 的参数对象必须是 Person 对象或其子类对象
        MethodHandle methodHandle = setNameMh.bindTo(student);
        methodHandle.invoke("test");
        Assert.assertEquals("test", student.getName());
    }

    @Test
    public void testInvokeExact() throws Throwable {
        MethodType sumMethodType = MethodType.methodType(int.class, Integer.class, Integer.class);
        MethodHandle sumMh = lookup.findVirtual(Person.class, "sum", sumMethodType);
        Person perSon = new Person();
        // 使用 invoke 成功执行
        Integer sum = (Integer) sumMh.invoke(perSon, 1, 2);
        Assert.assertEquals(3, sum.intValue());
    }
}
