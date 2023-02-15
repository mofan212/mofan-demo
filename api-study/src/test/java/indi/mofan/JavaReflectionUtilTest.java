package indi.mofan;

import indi.mofan.pojo.GetNameTestClass;
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
        System.out.println("基本类型: ");
        Class<Integer> intClazz = int.class;
        System.out.println(intClazz.getName());
        System.out.println(intClazz.getCanonicalName());
        System.out.println(intClazz.getSimpleName());
        System.out.println(intClazz.getTypeName());

        System.out.println("基本类型数组: ");
        Class<byte[]> byteClazz = byte[].class;
        System.out.println(byteClazz.getName());
        System.out.println(byteClazz.getCanonicalName());
        System.out.println(byteClazz.getSimpleName());
        System.out.println(byteClazz.getTypeName());

        System.out.println("字符串数组: ");
        Class<? extends String[]> stringArrayClazz = String[].class;
        System.out.println(stringArrayClazz.getName());
        System.out.println(stringArrayClazz.getCanonicalName());
        System.out.println(stringArrayClazz.getSimpleName());
        System.out.println(stringArrayClazz.getTypeName());

        System.out.println("普通类: ");
        Class<GetNameTestClass> clazz = GetNameTestClass.class;
        System.out.println(clazz.getName());
        System.out.println(clazz.getCanonicalName());
        System.out.println(clazz.getSimpleName());
        System.out.println(clazz.getTypeName());

        System.out.println("内部类: ");
        Class<GetNameTestClass.Inner> innerClazz = GetNameTestClass.Inner.class;
        System.out.println(innerClazz.getName());
        System.out.println(innerClazz.getCanonicalName());
        System.out.println(innerClazz.getSimpleName());
        System.out.println(innerClazz.getTypeName());

        System.out.println("匿名内部类: ");
        Class<? extends Runnable> runnableClazz = new Runnable() {
            @Override
            public void run() {

            }
        }.getClass();
        System.out.println(runnableClazz.getName());
        System.out.println(runnableClazz.getCanonicalName());
        System.out.println(runnableClazz.getSimpleName());
        System.out.println(runnableClazz.getTypeName());

        System.out.println("静态代码块本地类: ");
        GetNameTestClass obj = new GetNameTestClass();

        System.out.println("静态方法本地类: ");
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
