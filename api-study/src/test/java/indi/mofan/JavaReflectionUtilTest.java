package indi.mofan;

import indi.mofan.pojo.GetNameTestClass;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author mofan
 * @date 2023/2/15 10:44
 */
public class JavaReflectionUtilTest {
    @Test
    public void testGetName() {
        // 基本类型
        Class<Integer> intClazz = int.class;
        Assert.assertEquals("int", intClazz.getName());
        Assert.assertEquals("int", intClazz.getCanonicalName());
        Assert.assertEquals("int", intClazz.getSimpleName());
        Assert.assertEquals("int", intClazz.getTypeName());

        // 基本类型数组
        Class<byte[]> byteClazz = byte[].class;
        Assert.assertEquals("[B", byteClazz.getName());
        Assert.assertEquals("byte[]", byteClazz.getCanonicalName());
        Assert.assertEquals("byte[]", byteClazz.getSimpleName());
        Assert.assertEquals("byte[]", byteClazz.getTypeName());

        // 字符串数组
        Class<? extends String[]> stringArrayClazz = String[].class;
        Assert.assertEquals("[Ljava.lang.String;", stringArrayClazz.getName());
        Assert.assertEquals("java.lang.String[]", stringArrayClazz.getCanonicalName());
        Assert.assertEquals("String[]", stringArrayClazz.getSimpleName());
        Assert.assertEquals("java.lang.String[]", stringArrayClazz.getTypeName());

        // 普通类
        Class<GetNameTestClass> clazz = GetNameTestClass.class;
        Assert.assertEquals("indi.mofan.pojo.GetNameTestClass", clazz.getName());
        Assert.assertEquals("indi.mofan.pojo.GetNameTestClass", clazz.getCanonicalName());
        Assert.assertEquals("GetNameTestClass", clazz.getSimpleName());
        Assert.assertEquals("indi.mofan.pojo.GetNameTestClass", clazz.getTypeName());

        // 内部类
        Class<GetNameTestClass.Inner> innerClazz = GetNameTestClass.Inner.class;
        Assert.assertEquals("indi.mofan.pojo.GetNameTestClass$Inner", innerClazz.getName());
        Assert.assertEquals("indi.mofan.pojo.GetNameTestClass.Inner", innerClazz.getCanonicalName());
        Assert.assertEquals("Inner", innerClazz.getSimpleName());
        Assert.assertEquals("indi.mofan.pojo.GetNameTestClass$Inner", innerClazz.getTypeName());

        // 匿名内部类
        Class<? extends Runnable> runnableClazz = new Runnable() {
            @Override
            public void run() {

            }
        }.getClass();
        Assert.assertEquals("indi.mofan.JavaReflectionUtilTest$1", runnableClazz.getName());
        Assert.assertNull(runnableClazz.getCanonicalName());
        Assert.assertEquals("", runnableClazz.getSimpleName());
        Assert.assertEquals("indi.mofan.JavaReflectionUtilTest$1", runnableClazz.getTypeName());

        System.out.println("静态代码块本地类: ");
        GetNameTestClass obj = new GetNameTestClass();

        System.out.println("静态方法中的本地类: ");
        GetNameTestClass.fun();

        System.out.println("Lambda 表达式: ");
        Supplier<String> supplier = () -> "Function";
        Class<? extends Supplier> supplierClazz = supplier.getClass();
        System.out.println(supplierClazz.getName());
        System.out.println(supplierClazz.getCanonicalName());
        System.out.println(supplierClazz.getSimpleName());
        System.out.println(supplierClazz.getTypeName());

        System.out.println("方法引用: ");
        Function<Integer, String> fun = String::valueOf;
        Class<? extends Function> funClazz = fun.getClass();
        System.out.println(funClazz.getName());
        System.out.println(funClazz.getCanonicalName());
        System.out.println(funClazz.getSimpleName());
        System.out.println(funClazz.getTypeName());
    }
}
