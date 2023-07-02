package indi.mofan;

import indi.mofan.pojo.Person;
import indi.mofan.pojo.Student;
import lombok.SneakyThrows;
import org.assertj.core.api.HamcrestCondition;
import org.assertj.core.api.WithAssertions;
import org.assertj.core.data.Index;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author mofan
 * @date 2022/8/16 10:37
 */
public class MethodHandleTest implements WithAssertions {

    private final MethodHandles.Lookup lookup = MethodHandles.lookup();

    @Test
    public void testConstructor() throws Throwable {
        // 方法的签名
        MethodType noArgs = MethodType.methodType(void.class);
        // 指定类、方法的签名、哪一种方法
        MethodHandle noArgsMethodHandle = lookup.findConstructor(Person.class, noArgs);
        // invoke 反射调用
        Person noArgsPerson = (Person) noArgsMethodHandle.invoke();
        assertThat(noArgsPerson).isNotNull();

        // 第一个参数是返回值类型，后面的是参数列表的数据类型
        MethodType allArgs = MethodType.methodType(void.class, String.class, Integer.class);
        MethodHandle allArgsMethodHandle = lookup.findConstructor(Person.class, allArgs);
        Person allArgsPerson = (Person) allArgsMethodHandle.invoke("test", 18);
        assertThat(allArgsPerson).extracting(Person::getName, Person::getAge).containsExactly("test", 18);
    }

    @Test
    public void testPublicMethod() throws Throwable {
        // getName
        MethodType getNameMethodType = MethodType.methodType(String.class);
        MethodHandle getNameMethodHandle = lookup.findVirtual(Person.class, "getName", getNameMethodType);
        Person perSon = new Person("test", 18);
        String name = (String) getNameMethodHandle.invoke(perSon);
        assertThat(name).isEqualTo("test");

        // setAge
        MethodType setAgeMethodType = MethodType.methodType(void.class, Integer.class);
        MethodHandle setAgeMethodHandle = lookup.findVirtual(Person.class, "setAge", setAgeMethodType);
        setAgeMethodHandle.invoke(perSon, 100);
        assertThat(perSon).extracting(Person::getAge).isEqualTo(100);
    }

    @Test
    public void testPrivateMethod() throws Throwable {
        Method getStr = Person.class.getDeclaredMethod("getStr", String.class, Integer.class);
        getStr.setAccessible(true);
        MethodHandle getStrMh = lookup.unreflect(getStr);
        String str = (String) getStrMh.invoke(new Person("test", 18), "TEST", 20);
        assertThat(str).isEqualTo("TEST - 20");
    }

    @Test
    public void testPublicStaticMethod() throws Throwable {
        MethodType returnIntMethodType = MethodType.methodType(Integer.class, Integer.class);
        MethodHandle returnIntMh = lookup.findStatic(Person.class, "returnInt", returnIntMethodType);
        Integer result = (Integer) returnIntMh.invoke(2);
        assertThat(result).isEqualTo(2);
    }

    @Test
    public void testPublicProperties() throws Throwable {
        MethodHandle boolMh = lookup.findGetter(Person.class, "bool", Boolean.class);
        Person perSon = new Person();
        perSon.setBool(true);
        Boolean bool = (Boolean) boolMh.invoke(perSon);
        assertThat(bool).isTrue();

        // 获取私有字段的 Getter
        assertThatExceptionOfType(IllegalAccessException.class)
                .isThrownBy(() -> lookup.findGetter(Person.class, "name", String.class));
    }

    @Test
    public void testPrivateProperties() throws Throwable {
        Field field = Person.class.getDeclaredField("name");
        field.setAccessible(true);
        MethodHandle nameMh = lookup.unreflectGetter(field);
        String name = (String) nameMh.invoke(new Person("test", 18));
        assertThat(name).isEqualTo("test");
    }

    @Test
    public void testBindTo() throws Throwable {
        MethodType setNameMethodType = MethodType.methodType(void.class, String.class);
        MethodHandle setNameMh = lookup.findVirtual(Person.class, "setName", setNameMethodType);
        Student student = new Student();
        // bindTo 的参数对象必须是 Person 对象或其子类对象
        MethodHandle methodHandle = setNameMh.bindTo(student);
        methodHandle.invoke("test");
        assertThat(student).extracting(Student::getName).isEqualTo("test");

        // 绑定一个参数，但是不执行 MethodHandle
        MethodType concatMethodType = MethodType.methodType(String.class, String.class);
        MethodHandle concat = lookup.findVirtual(String.class, "concat", concatMethodType);
        methodHandle = concat.bindTo("hello ");
        assertThat(methodHandle.invoke("world")).isEqualTo("hello world");
    }

    @Test
    @SuppressWarnings("all")
    public void testInvokeExact() throws Throwable {
        MethodType sumMethodType = MethodType.methodType(long.class, int.class, long.class);
        MethodHandle sumMh = lookup.findVirtual(Person.class, "sum", sumMethodType);
        Person perSon = new Person();
        // 使用 invoke 成功执行
        Object sum = sumMh.invoke(perSon, Integer.valueOf(1), 2);
        assertThat(sum).isEqualTo(3L);

        // invokeExact 将更严格地执行
        assertThatExceptionOfType(WrongMethodTypeException.class).isThrownBy(() -> {
            Object result = sumMh.invokeExact(perSon, 1, 2L);
        });
        assertThatExceptionOfType(WrongMethodTypeException.class).isThrownBy(() -> {
            sumMh.invokeExact(perSon, Integer.valueOf(1), 2L);
        });
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("all")
    public void testInvokeResult() {
        MethodType methodType = MethodType.methodType(void.class, String.class);
        MethodHandle print = lookup.findVirtual(Person.class, "print", methodType);
        Person person = new Person();

        // 没有返回值的方法给了返回值，返回 null
        Object result = print.invoke(person, "Hello World");
        assertThat(result).isNull();

        // 以基本类型接收，返回基本类型的默认值
        int intResult = (int) print.invoke(person, "Hello World");
        assertThat(intResult).isEqualTo(0);
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("all")
    public void testInvokeWithArguments() {
        MethodType subtractMethodType = MethodType.methodType(int.class, int.class, int.class);
        MethodHandle subtractMh = lookup.findVirtual(Person.class, "subtract", subtractMethodType);
        Person person = new Person();
        Object two = 2;
        Object one = 1;
        List<Object> arguments = new ArrayList<>();
        arguments.add(person);
        arguments.addAll(List.of(new Object[]{2, 1}));

        // invokeExact
        int result = (int) subtractMh.invokeExact(person, 2, 1);
        assertThat(result).isEqualTo(1);
        assertThatExceptionOfType(WrongMethodTypeException.class)
                .isThrownBy(() -> subtractMh.invokeExact(person, two, one));
        assertThatExceptionOfType(WrongMethodTypeException.class)
                .isThrownBy(() -> subtractMh.invokeExact(arguments));

        // invoke
        result = (int) subtractMh.invoke(person, 2, 1);
        assertThat(result).isEqualTo(1);
        result = (int) subtractMh.invoke(person, two, one);
        assertThat(result).isEqualTo(1);
        assertThatExceptionOfType(WrongMethodTypeException.class)
                .isThrownBy(() -> subtractMh.invoke(arguments));

        // invokeWithArguments
        result = (int) subtractMh.invokeWithArguments(person, 2, 1);
        assertThat(result).isEqualTo(1);
        result = (int) subtractMh.invokeWithArguments(person, two, one);
        assertThat(result).isEqualTo(1);
        // 可以传入参数列表
        result = (int) subtractMh.invokeWithArguments(arguments);
        assertThat(result).isEqualTo(1);
        assertThatExceptionOfType(WrongMethodTypeException.class)
                .isThrownBy(() -> subtractMh.invokeWithArguments(person, new Object[]{2, 1}))
                .withMessage("cannot convert MethodHandle(Person,int,int)int to (Object,Object)Object");
    }

    static class Varargs {
        public void method(Object... objects) {
        }
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("all")
    public void testAsVarargsCollector() {
        MethodHandle deepToString = lookup.findStatic(Arrays.class, "deepToString", MethodType.methodType(
                String.class, Object[].class
        ));
        // 将最后一个数组类型的参数转换成对应类型的可变参数，invoke 时无需使用原始的数组形式
        MethodHandle mh = deepToString.asVarargsCollector(Object[].class);
        assertThat(((String) mh.invokeExact(new Object[]{"won"}))).isEqualTo("[won]");
        assertThat(((String) mh.invoke(new Object[]{"won"}))).isEqualTo("[won]");
        assertThat(((String) mh.invoke("won"))).isEqualTo("[won]");
        assertThatExceptionOfType(WrongMethodTypeException.class)
                .isThrownBy(() -> {
                    String str = (String) mh.invokeExact("won");
                });
        assertThat(((String) mh.invoke((Object) new Object[]{"won"}))).isEqualTo("[[won]]");

        // Arrays#asList 默认支持
        MethodHandle asList = lookup.findStatic(Arrays.class, "asList", MethodType.methodType(
                List.class, Object[].class
        ));
        assertThat(asList.isVarargsCollector()).isTrue();
        assertThat(asList.invoke().toString()).isEqualTo("[]");
        assertThat(asList.invoke("1").toString()).isEqualTo("[1]");
        String[] args = {"1", "2", "3"};
        assertThat(asList.invoke(args).toString()).isEqualTo("[1, 2, 3]");
        assertThat(asList.invoke((Object[]) args).toString()).isEqualTo("[1, 2, 3]");
        List<?> list = (List<?>) asList.invoke((Object) args);
        assertThat(list).hasSize(1).has(HamcrestCondition.matching(Is.is(new String[]{"1", "2", "3"})), Index.atIndex(0));

        // 可变参数方法默认支持
        MethodHandle method = lookup.findVirtual(Varargs.class, "method", MethodType.methodType(
                void.class, Object[].class
        ));
        assertThat(method.isVarargsCollector()).isTrue();
    }

    @Test
    @SneakyThrows
    public void testAsCollector() {
        MethodType methodType = MethodType.methodType(String.class, Object[].class);
        MethodHandle deepToString = lookup.findStatic(Arrays.class, "deepToString", methodType);
        assertThat(((String) deepToString.invokeExact(new Object[]{"java"}))).isEqualTo("[java]");

        // 与 asVarargsCollector() 方法类似，只不过 asCollector() 只会收集指定数量的参数
        MethodHandle ts1 = deepToString.asCollector(Object[].class, 1);
        assertThat(((String) ts1.invokeExact((Object) new Object[]{"java"}))).isNotEqualTo("[java]").isEqualTo("[[java]]");
        assertThat(((String) ts1.invokeExact((Object) "java"))).isEqualTo("[java]");
        assertThatExceptionOfType(WrongMethodTypeException.class)
                .isThrownBy(() -> {
                    String str = ((String) ts1.invokeExact("hello", "world"));
                });

        // 数组类型可以是 Object[] 的子类
        MethodHandle ts2 = deepToString.asCollector(String[].class, 2);
        assertThat(ts2.type()).isEqualTo(MethodType.methodType(String.class, String.class, String.class));
        assertThat(((String) ts2.invokeExact("one", "two"))).isEqualTo("[one, two]");

        MethodHandle ts0 = deepToString.asCollector(Object[].class, 0);
        assertThat(((String) ts0.invokeExact())).isEqualTo("[]");

        // 可以嵌套
        MethodHandle ts22 = deepToString.asCollector(Object[].class, 3).asCollector(String[].class, 2);
        assertThat(((String) ts22.invokeExact((Object) 'A', (Object) "B", "C", "D")))
                .isEqualTo("[A, B, [C, D]]");

        // 数组类型可以是任意基本类型
        MethodHandle byteToString = lookup.findStatic(Arrays.class, "toString", MethodType.methodType(String.class, byte[].class))
                .asCollector(byte[].class, 3);
        assertThat(((String) byteToString.invokeExact((byte) 1, (byte) 2, (byte) 3))).isEqualTo("[1, 2, 3]");
        MethodHandle longToString = lookup.findStatic(Arrays.class, "toString", MethodType.methodType(String.class, long[].class))
                .asCollector(long[].class, 1);
        assertThat(((String) longToString.invokeExact((long) 212))).isEqualTo("[212]");
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("all")
    public void testAsSpreader() {
        MethodType addExactMethodType = MethodType.methodType(int.class, int.class, int.class);
        MethodHandle addExact = lookup.findStatic(Math.class, "addExact", addExactMethodType);

        int[] args = {1, 1};
        assertThatNoException().isThrownBy(() -> addExact.invoke(1, 1));
        assertThatNoException().isThrownBy(() -> {
            int result = (int) addExact.invokeExact(1, 1);
        });
        // 不接受参数数组
        assertThatExceptionOfType(WrongMethodTypeException.class)
                .isThrownBy(() -> addExact.invoke(args));
        assertThatExceptionOfType(WrongMethodTypeException.class)
                .isThrownBy(() -> {
                    int result = (int) addExact.invokeExact(args);
                });

        // asSpreader() 与 asVarargsCollector()、asCollector() 相反，在执行 invoke() 方法时将长度可变的参数转换成数组
        MethodHandle addExactAsSpreader = addExact.asSpreader(int[].class, 2);
        assertThatNoException().isThrownBy(() -> addExactAsSpreader.invoke(args));
        assertThatNoException().isThrownBy(() -> {
            int result = (int) addExactAsSpreader.invokeExact(args);
        });

        Object arguments = new int[]{1, 1};
        assertThatExceptionOfType(WrongMethodTypeException.class)
                .isThrownBy(() -> {
                    int result = (int) addExactAsSpreader.invokeExact(arguments);
                });
        assertThatNoException().isThrownBy(() -> addExactAsSpreader.invoke(arguments));

        // 另一种使用示例
        MethodType equalsMethodType = MethodType.methodType(boolean.class, Object.class);
        MethodHandle equals = lookup.findVirtual(String.class, "equals", equalsMethodType);
        MethodHandle methodHandle = equals.asSpreader(Object[].class, 2);
        assertThat(((boolean) methodHandle.invoke(new Object[]{"java", "java"}))).isTrue();
    }
}
