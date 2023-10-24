package indi.mofan.reflection;

import indi.mofan.pojo.GetNameTestClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author mofan
 * @date 2023/2/15 10:44
 */
public class JavaReflectionUtilTest {
    @Test
    @SuppressWarnings({"ConstantValue", "InstantiationOfUtilityClass"})
    public void testGetName() {
        // 基本类型
        Class<Integer> intClazz = int.class;
        Assertions.assertEquals("int", intClazz.getName());
        Assertions.assertEquals("int", intClazz.getCanonicalName());
        Assertions.assertEquals("int", intClazz.getSimpleName());
        Assertions.assertEquals("int", intClazz.getTypeName());

        // 基本类型数组
        Class<byte[]> byteClazz = byte[].class;
        Assertions.assertEquals("[B", byteClazz.getName());
        Assertions.assertEquals("byte[]", byteClazz.getCanonicalName());
        Assertions.assertEquals("byte[]", byteClazz.getSimpleName());
        Assertions.assertEquals("byte[]", byteClazz.getTypeName());

        // 字符串数组
        Class<? extends String[]> stringArrayClazz = String[].class;
        Assertions.assertEquals("[Ljava.lang.String;", stringArrayClazz.getName());
        Assertions.assertEquals("java.lang.String[]", stringArrayClazz.getCanonicalName());
        Assertions.assertEquals("String[]", stringArrayClazz.getSimpleName());
        Assertions.assertEquals("java.lang.String[]", stringArrayClazz.getTypeName());

        // 普通类
        Class<GetNameTestClass> clazz = GetNameTestClass.class;
        Assertions.assertEquals("indi.mofan.pojo.GetNameTestClass", clazz.getName());
        Assertions.assertEquals("indi.mofan.pojo.GetNameTestClass", clazz.getCanonicalName());
        Assertions.assertEquals("GetNameTestClass", clazz.getSimpleName());
        Assertions.assertEquals("indi.mofan.pojo.GetNameTestClass", clazz.getTypeName());

        // 内部类
        Class<GetNameTestClass.Inner> innerClazz = GetNameTestClass.Inner.class;
        Assertions.assertEquals("indi.mofan.pojo.GetNameTestClass$Inner", innerClazz.getName());
        Assertions.assertEquals("indi.mofan.pojo.GetNameTestClass.Inner", innerClazz.getCanonicalName());
        Assertions.assertEquals("Inner", innerClazz.getSimpleName());
        Assertions.assertEquals("indi.mofan.pojo.GetNameTestClass$Inner", innerClazz.getTypeName());

        // 匿名内部类
        Class<? extends Runnable> runnableClazz = new Runnable() {
            @Override
            public void run() {

            }
        }.getClass();
        Assertions.assertEquals("indi.mofan.reflection.JavaReflectionUtilTest$1", runnableClazz.getName());
        Assertions.assertNull(runnableClazz.getCanonicalName());
        Assertions.assertEquals("", runnableClazz.getSimpleName());
        Assertions.assertEquals("indi.mofan.reflection.JavaReflectionUtilTest$1", runnableClazz.getTypeName());

        System.out.println("静态代码块本地类: ");
        GetNameTestClass obj = new GetNameTestClass();

        System.out.println("静态方法中的本地类: ");
        GetNameTestClass.fun();

        System.out.println("Lambda 表达式: ");
        Supplier<String> supplier = () -> "Function";
        var supplierClazz = supplier.getClass();
        System.out.println(supplierClazz.getName());
        System.out.println(supplierClazz.getCanonicalName());
        System.out.println(supplierClazz.getSimpleName());
        System.out.println(supplierClazz.getTypeName());

        System.out.println("方法引用: ");
        Function<Integer, String> fun = String::valueOf;
        var funClazz = fun.getClass();
        System.out.println(funClazz.getName());
        System.out.println(funClazz.getCanonicalName());
        System.out.println(funClazz.getSimpleName());
        System.out.println(funClazz.getTypeName());
    }
}
