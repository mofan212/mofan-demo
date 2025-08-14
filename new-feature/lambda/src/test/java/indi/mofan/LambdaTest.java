package indi.mofan;

import indi.mofan.domain.Person;
import indi.mofan.lambda.SFunction;
import indi.mofan.lambda.SSupplier;
import indi.mofan.pojo.Child;
import indi.mofan.pojo.Parent;
import indi.mofan.spi.Runnable;
import indi.mofan.util.ReflectionUtil;
import jdk.internal.org.objectweb.asm.Type;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import static indi.mofan.util.LambdaUtil.getImplClass;

/**
 * @author mofan
 * @date 2022/6/9 10:47
 */
public class LambdaTest {

    @Test
    public void testSerializedLambda() throws Exception {
        SFunction<Person, String> methodReferenceFunc = Person::getName;
        Method methodReferenceMethod = methodReferenceFunc.getClass().getDeclaredMethod("writeReplace");
        methodReferenceMethod.setAccessible(true);
        SerializedLambda methodReferenceLambda = (SerializedLambda) methodReferenceMethod.invoke(methodReferenceFunc);

        SFunction<Person, String> lambdaFunc = (person) -> person.getName();
        Method lambdaMethod = lambdaFunc.getClass().getDeclaredMethod("writeReplace");
        lambdaMethod.setAccessible(true);
        SerializedLambda lambda = (SerializedLambda) lambdaMethod.invoke(lambdaFunc);

        System.out.println(); // 此处断点
    }

    @Test
    public void testSerialLambda() throws Exception {
        SFunction<Person, String> methodReferenceFunc = Person::getName;
        Class<?> methodReferenceClass = serial(methodReferenceFunc);

        SFunction<Person, String> lambdaFunc = (person) -> person.getName();
        Class<?> lambdaClass = serial(lambdaFunc);

        System.out.println(); // 此处断点
    }

    private Class<?> serial(Serializable serializable) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(serializable);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        return ois.readObject().getClass();
    }

    @Test
    public void testCreateSerializedLambdaByAnotherWay() throws Exception {
        SFunction<Person, String> function = Person::getName;
        indi.mofan.lambda.SerializedLambda serializedLambda = getSerializedLambda(function);


        Assertions.assertEquals("getName", serializedLambda.getImplMethodName());
    }

    private static indi.mofan.lambda.SerializedLambda getSerializedLambda(Serializable serializable) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(serializable);
            oos.flush();
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())) {
                // 重写 resolveClass 方法，半路截胡，使反序列化返回的类型为 indi.mofan.lambda.SerializedLambda
                @Override
                protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                    Class<?> klass = super.resolveClass(desc);
                    return klass == java.lang.invoke.SerializedLambda.class ? indi.mofan.lambda.SerializedLambda.class : klass;
                }
            }) {
                return (indi.mofan.lambda.SerializedLambda) ois.readObject();
            }
        }
    }

    @Test
    public void testGet$deserializeLambda$MethodInfo() throws Exception {
        SFunction<Person, String> function = Person::getName;
        Method writeReplaceMethod = function.getClass().getDeclaredMethod("writeReplace");
        writeReplaceMethod.setAccessible(true);
        SerializedLambda serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(function);

        // 反射获取“捕获类”对应的 Class 对象
        String capturingClassName = serializedLambda.getCapturingClass().replace("/", ".");
        Assertions.assertEquals(this.getClass().getName(), capturingClassName);
        Class<?> capturingClass = Class.forName(capturingClassName);

        // 调用 Spring 的方法
        ReflectionUtils.doWithMethods(capturingClass, method -> {
            Assertions.assertEquals("$deserializeLambda$", method.getName());
            Assertions.assertEquals("private static", Modifier.toString(method.getModifiers()));
            Assertions.assertEquals(1, method.getParameterCount());
            Assertions.assertEquals(SerializedLambda.class.getName(), method.getParameterTypes()[0].getName());
            Assertions.assertEquals(Object.class.getName(), method.getReturnType().getName());
        }, method -> Objects.equals(method.getName(), "$deserializeLambda$"));
    }

    @Test
    public void testGetImplClass() throws Exception {

        Person person = new Person();
        SSupplier<String> consumer = person::getName;
        Assertions.assertEquals("indi.mofan.domain.Person", getImplClass(consumer));

        SSupplier<String> supplier = new SSupplier<String>() {
            @Override
            public String get() {
                return person.getName();
            }
        };
        Assertions.assertTrue(getImplClass(supplier).isEmpty());
    }

    @Test
    public void testIntrospector() throws Exception {
        // 获取整个 bean 信息
//        BeanInfo personBeanInfo = Introspector.getBeanInfo(Person.class);
        // 检索到 Object 时停止检索，可以检索到 JavaBean 的任意父类时停止
        BeanInfo beanInfo = Introspector.getBeanInfo(Person.class, Object.class);

        // 获取属性信息
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        List<String> beanFieldInfo = Arrays.stream(propertyDescriptors).map(PropertyDescriptor::getName).toList();
        List<String> classFieldInfo = Arrays.stream(Person.class.getDeclaredFields()).map(Field::getName).toList();
        Assertions.assertTrue(classFieldInfo.containsAll(beanFieldInfo));
        Assertions.assertEquals(2, beanFieldInfo.size());

        // 获取 bean 中 public 的方法
        MethodDescriptor[] methodInfo = beanInfo.getMethodDescriptors();
        Assertions.assertEquals(4, methodInfo.length);


        // 获取属性的值
        Person person = new Person();
        person.setName("mofan");
        String propertyName = "name";
        PropertyDescriptor nameProperty = new PropertyDescriptor(propertyName, Person.class);
        Assertions.assertEquals("mofan", nameProperty.getReadMethod().invoke(person));
        // 修改属性的值
        nameProperty.getWriteMethod().invoke(person, "TestName");
        Assertions.assertEquals("TestName", nameProperty.getReadMethod().invoke(person));
        // 原对象也有影响
        Assertions.assertEquals("TestName", person.getName());
    }

    @Test
    public void testGetAllBeanInfo() throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(Person.class);

        // 获取所有属性
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        Assertions.assertEquals(3, propertyDescriptors.length);
    }

    @Test
    public void testGetFieldsAndProperties() throws Exception {
        // 获取 Fields
        Field[] fields = Person.class.getDeclaredFields();

        // 获取 Properties
        BeanInfo beanInfo = Introspector.getBeanInfo(Person.class, Object.class);
        PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
    }

    @Test
    public void testReflectionUtil() {
        SFunction<Person, String> function = Person::getName;
        String fieldName = ReflectionUtil.getFieldName(function);
        Assertions.assertEquals("name", fieldName);
    }

    @Test
    public void testSPI() {
        ServiceLoader<Runnable> loader = ServiceLoader.load(indi.mofan.spi.Runnable.class);
        for (Runnable runner : loader) {
            runner.run();
        }
    }

//    static {
//        System.setProperty("jdk.internal.lambda.dumpProxyClasses", ".");
//    }

    @Test
    public void testShowImplClass() {
        SFunction<Person, String> fun1 = Person::getName;
        SFunction<Person, String> fun2 = Person::getName;

        Assertions.assertNotSame(fun1, fun2);
        Assertions.assertNotEquals(fun1, fun2);
        Assertions.assertNotSame(fun1.getClass(), fun2.getClass());
    }

    @Test
    public void testCallSFunction() {
        SFunction<Person, String> function1 = Person::getName;
        SFunction<Person, String> function2 = Person::getName;

        Assertions.assertNotSame(function1, function2);

        List<SFunction<Person, String>> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            SFunction<Person, String> function = Person::getName;
            System.out.println(function);
            list.add(function);
        }
        Assertions.assertSame(list.get(0), list.get(1));
        Assertions.assertSame(list.get(1), list.get(2));
        Assertions.assertSame(list.get(2), list.get(0));
    }
    
    @Test
    public void testGetModifierInfo() throws Exception {
        Assertions.assertEquals("java/lang/String", Type.getInternalName(String.class));
        Assertions.assertEquals("java/util/Map", Type.getInternalName(Map.class));

        Assertions.assertEquals("Ljava/lang/String;", Type.getDescriptor(String.class));

        Assertions.assertEquals("I", Type.INT_TYPE.getDescriptor());

        Method setNameMethod = Person.class.getDeclaredMethod("setName", String.class);
        Assertions.assertEquals("(Ljava/lang/String;)V", Type.getMethodDescriptor(setNameMethod));
    }

    @Test
    public void testDeclared() {
        Class<Parent> parentClass = Parent.class;
        Class<Child> childClass = Child.class;

        // 当前类及其父类中所有 public 的字段
        Assertions.assertEquals(1, parentClass.getFields().length);
        Assertions.assertTrue(Arrays.stream(parentClass.getFields()).allMatch(i -> "publicParentField".equals(i.getName())));
        // 当前类中所有的字段
        List<String> parentFieldNameList = Arrays.asList("privateParentField", "publicParentField");
        Assertions.assertEquals(parentFieldNameList.size(), parentClass.getDeclaredFields().length);
        Assertions.assertTrue(Arrays.stream(parentClass.getDeclaredFields()).map(Field::getName).allMatch(parentFieldNameList::contains));

        // 当前类及其父类中所有 public 的字段
        List<String> childPublicFieldNameList = Arrays.asList("publicChildField", "publicParentField", "CONSTANT");
        Assertions.assertEquals(childPublicFieldNameList.size(), childClass.getFields().length);
        Assertions.assertTrue(Arrays.stream(childClass.getFields()).map(Field::getName).allMatch(childPublicFieldNameList::contains));
        // 当前类中所有的字段（实现的接口中的常量不在其中）
        List<String> childFiledNameList = Arrays.asList("privateChildField", "publicChildField");
        Assertions.assertEquals(childFiledNameList.size(), childClass.getDeclaredFields().length);
        Assertions.assertTrue(Arrays.stream(childClass.getDeclaredFields()).map(Field::getName).allMatch(childFiledNameList::contains));

        // 当前类及其父类中所有 public 的方法（因此包含 Object 类中 public 的方法）
        List<String> parentPublicMethodNameList = Arrays.asList("setPrivateParentField", "getPublicParentField",
                "getPrivateParentField", "setPublicParentField");
        Assertions.assertTrue(parentClass.getMethods().length > 4);
        Assertions.assertEquals(Object.class.getMethods().length + 4, parentClass.getMethods().length);
        Assertions.assertTrue(Arrays.stream(parentClass.getMethods()).map(Method::getName).collect(Collectors.toList()).containsAll(parentPublicMethodNameList));
        // 当前类中所有的方法
        Assertions.assertEquals(parentPublicMethodNameList.size(), parentClass.getDeclaredMethods().length);
        Assertions.assertTrue(Arrays.stream(parentClass.getDeclaredMethods()).map(Method::getName).allMatch(parentPublicMethodNameList::contains));

        // 当前类及其父类中所有 public 的方法
        List<String> childPublicMethodNameList = Arrays.asList("add", "setPublicChildField", "getPublicChildField",
                "setPrivateChildField", "getPrivateChildField");
        Assertions.assertTrue(childClass.getMethods().length > childPublicMethodNameList.size());
        Assertions.assertEquals(parentClass.getMethods().length + childPublicMethodNameList.size(), childClass.getMethods().length);
        Assertions.assertTrue(Arrays.stream(childClass.getMethods()).map(Method::getName).collect(Collectors.toList()).containsAll(childPublicMethodNameList));

        // 当前类中所有的方法
        Assertions.assertEquals(childPublicMethodNameList.size(), childClass.getDeclaredMethods().length);
        Assertions.assertTrue(Arrays.stream(childClass.getDeclaredMethods()).map(Method::getName).allMatch(childPublicMethodNameList::contains));
    }


}
