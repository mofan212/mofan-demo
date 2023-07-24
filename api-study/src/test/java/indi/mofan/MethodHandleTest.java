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
import java.lang.invoke.MethodHandleProxies;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.WrongMethodTypeException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * @author mofan
 * @date 2022/8/16 10:37
 */
public class MethodHandleTest implements WithAssertions {

    private final MethodHandles.Lookup lookup = MethodHandles.lookup();

    @Test
    @SneakyThrows
    public void testQuickStart() {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        // concat(String)：返回 String，也接收一个 String
        MethodType concatMethodType = MethodType.methodType(String.class, String.class);
        // 目标方法在 String 类中，名为 concat
        MethodHandle concat = lookup.findVirtual(String.class, "concat", concatMethodType);
        // 执行方法句柄，并强转返回值
        String result = (String) concat.invoke("Hello ", "World");
        assertThat(result).isEqualTo("Hello World");
    }

    @Test
    @SneakyThrows
    public void testInvokeLambda() {
        Function<Integer, Integer> increase = integer -> integer + 1;
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        // 由于泛型擦除均使用 Object
        MethodType methodType = MethodType.methodType(Object.class, Object.class);
        MethodHandle apply = lookup.findVirtual(increase.getClass(), "apply", methodType);
        Object result = apply.invoke(increase, 1);
        assertThat(result).isInstanceOf(Integer.class)
                .isEqualTo(2);
    }

    @Test
    public void testCreateMethodType() {
        // 指定返回值和参数类型显示创建
        MethodType mt1 = MethodType.methodType(int.class);  // String#length()
        MethodType mt2 = MethodType.methodType(String.class, String.class); // String#concat(String)
        // 以另一个 MethodType 的参数作为当前 MethodType 的参数
        MethodType mt3 = MethodType.methodType(boolean.class, mt2); // String#contains()

        // 生成通用的 MethodType
        assertThat(MethodType.genericMethodType(2))
                .isEqualTo(MethodType.methodType(Object.class, Object.class, Object.class));
        assertThat(MethodType.genericMethodType(1, true))
                .isEqualTo(MethodType.methodType(Object.class, Object.class, Object[].class));

        // 使用方法描述符创建
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        assertThat(MethodType.fromMethodDescriptorString("(Ljava/lang/String;)Ljava/lang/String;", loader))
                .isEqualTo(MethodType.methodType(String.class, String.class));
        assertThat(MethodType.fromMethodDescriptorString("(IF)V", loader))
                .isEqualTo(MethodType.methodType(void.class, int.class, float.class));
        assertThat(MethodType.fromMethodDescriptorString("(ILjava/lang/String;)[I", loader))
                .isEqualTo(MethodType.methodType(int[].class, int.class, String.class));
    }

    @Test
    public void testModifyMethodType() {
        // MethodType 是不变的，每次修改都会产生新的 MethodType，就像 String 一样
        MethodType methodType = MethodType.methodType(String.class, int.class, int.class); // String#substring(int, int)
        // 添加一个参数类型
        assertThat(methodType.appendParameterTypes(String.class))
                .isEqualTo(MethodType.methodType(String.class, int.class, int.class, String.class));
        // 指定索引位置添加参数
        MethodType mt = methodType.insertParameterTypes(1, float.class, double.class);
        assertThat(mt).isEqualTo(MethodType.methodType(String.class, int.class, float.class, double.class, int.class));
        // 删除某个范围的参数
        assertThat(mt.dropParameterTypes(0, 2))
                .isEqualTo(MethodType.methodType(String.class, double.class, int.class));
        // 修改指定位置的参数
        assertThat(methodType.changeParameterType(0, long.class))
                .isEqualTo(MethodType.methodType(String.class, long.class, int.class));
        // 修改返回值类型
        assertThat(methodType.changeReturnType(void.class))
                .isEqualTo(MethodType.methodType(void.class, int.class, int.class));

        // 一次性修改所有的
        MethodType unwrapMt = MethodType.methodType(int.class, long.class, double.class, String.class);
        // 包装类型与基本类型的转换
        MethodType wrapMt = MethodType.methodType(Integer.class, Long.class, Double.class, String.class);
        assertThat(unwrapMt.wrap()).isEqualTo(wrapMt);
        assertThat(wrapMt.unwrap()).isEqualTo(unwrapMt);
        // 全部变为 Object 类型
        assertThat(unwrapMt.generic()).isEqualTo(MethodType.methodType(Object.class, Object.class, Object.class, Object.class));
        // 只引用类型变 Object 类型
        assertThat(unwrapMt.erase()).isEqualTo(MethodType.methodType(int.class, long.class, double.class, Object.class));
    }

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

    static class MyClass {
        private String privateMethod(int i) {
            return String.valueOf(i);
        }
    }

    @Test
    @SneakyThrows
    public void testFindSpecial() {
        MethodType methodType = MethodType.methodType(String.class, int.class);
        // 最后一个参数用于指定执行方法句柄时传入的对象必须具有访问私有方法的权限
        assertThatExceptionOfType(IllegalAccessException.class)
                .isThrownBy(() -> lookup.findSpecial(MyClass.class, "privateMethod", methodType, MyClass.class))
                .withMessageContaining("no private access for invokespecial");
        // 注意权限问题
        MethodHandle privateMethod = MethodHandles.privateLookupIn(MyClass.class, lookup)
                .findSpecial(MyClass.class, "privateMethod", methodType, MyClass.class);
        assertThat(privateMethod.invoke(new MyClass(), 212)).asString().isEqualTo("212");
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
    public void testUnreflect() throws Throwable {
        // 私有构造方法
        Constructor<Person> constructor = Person.class.getDeclaredConstructor(String.class);
        constructor.setAccessible(true);
        MethodHandle constructorMh = lookup.unreflectConstructor(constructor);
        assertThat(((Person) constructorMh.invoke("java"))).extracting(Person::getName).isEqualTo("java");

        // 私有方法
        Method getStr = Person.class.getDeclaredMethod("getStr", String.class, Integer.class);
        getStr.setAccessible(true);
        MethodHandle getStrMh = lookup.unreflect(getStr);
        String str = (String) getStrMh.invoke(new Person("test", 18), "TEST", 20);
        assertThat(str).isEqualTo("TEST - 20");
        // 使用 unreflectSpecial
        assertThatExceptionOfType(IllegalAccessException.class)
                .isThrownBy(() -> lookup.unreflectSpecial(getStr, Person.class))
                .withMessageStartingWith("no private access for invokespecial");
        MethodHandle getStrMh2 = MethodHandles.privateLookupIn(Person.class, lookup).unreflectSpecial(getStr, Person.class);
        str = (String) getStrMh2.invoke(new Person("test", 18), "TEST", 20);
        assertThat(str).isEqualTo("TEST - 20");

        // 私有字段
        Field field = Person.class.getDeclaredField("name");
        field.setAccessible(true);
        MethodHandle nameMh = lookup.unreflectGetter(field);
        String name = (String) nameMh.invoke(new Person("test", 18));
        assertThat(name).isEqualTo("test");
    }

    @Test
    @SneakyThrows
    public void testArrayHandle() {
        int[] arrays = {1, 2, 3, 4, 5, 6};
        MethodHandle getter = MethodHandles.arrayElementGetter(int[].class);
        assertThat(((int) getter.invoke(arrays, 1))).isEqualTo(2);
        MethodHandle setter = MethodHandles.arrayElementSetter(int[].class);
        setter.invoke(arrays, 1, 212);
        assertThat(arrays[1]).isEqualTo(212);
    }

    @Test
    @SneakyThrows
    public void testIdentity() {
        MethodHandle stringMh = MethodHandles.identity(String.class);
        assertThat(stringMh.invoke("java")).isEqualTo("java");

        MethodHandle personMh = MethodHandles.identity(Person.class);
        Person java = new Person("java", 100);
        assertThat(((Person) personMh.invoke(java)))
                .extracting(Person::getName, Person::getAge)
                .containsExactly("java", 100);
    }

    @Test
    @SneakyThrows
    public void testConstant() {
        MethodHandle stringMh = MethodHandles.constant(String.class, "java");
        assertThat(stringMh.invoke()).isEqualTo("java");

        Person java = new Person("java", 100);
        MethodHandle personMh = MethodHandles.constant(Person.class, java);
        assertThat(((Person) personMh.invoke()))
                .extracting(Person::getName, Person::getAge)
                .containsExactly("java", 100);

        assertThatExceptionOfType(ClassCastException.class)
                .isThrownBy(() -> MethodHandles.constant(long.class, "abc"));
        assertThatExceptionOfType(ClassCastException.class)
                .isThrownBy(() -> MethodHandles.constant(String.class, 123));
        MethodHandle mh = MethodHandles.constant(long.class, 123456);
        assertThat(mh.invoke()).isInstanceOf(Long.class).isEqualTo(123456L);
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

        // 多次绑定
        MethodType indexOfMethodType = MethodType.methodType(int.class, String.class);
        MethodHandle indexOf = lookup.findVirtual(String.class, "indexOf", indexOfMethodType)
                .bindTo("hello world").bindTo("e");
        assertThat(indexOf.invoke()).isEqualTo(1);

        // 绑定基本类型
        MethodType substringMethodType = MethodType.methodType(String.class, int.class, int.class);
        MethodHandle substring = lookup.findVirtual(String.class, "substring", substringMethodType);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> substring.bindTo("java").bindTo(2).bindTo(3))
                .withMessage("no leading reference parameter");
        // 对于基本类型的绑定需要使用 wrap() 包装下
        MethodHandle mh = substring.asType(substring.type().wrap())
                .bindTo("java").bindTo(2).bindTo(3);
        assertThat(mh.invoke()).isEqualTo("v");
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

    @Test
    @SneakyThrows
    @SuppressWarnings("unchecked, rawtypes")
    public void testAsFixedArity() {
        MethodType methodType = MethodType.methodType(List.class, Object[].class);
        MethodHandle asList = lookup.findStatic(Arrays.class, "asList", methodType).asVarargsCollector(Object[].class);
        assertThat(asList.invoke(1, 2, 3).toString()).isEqualTo("[1, 2, 3]");

        // 将参数长度可变的方法转换成参数长度不变的方法，即调用方法句柄时只能使用数组作为方法参数
        MethodHandle asListFix = asList.asFixedArity();
        assertThatExceptionOfType(WrongMethodTypeException.class)
                .isThrownBy(() -> asListFix.invoke(1, 2, 3));
        Object[] args = {1, 2, 3};
        assertThat(asListFix.invoke(args).toString()).isEqualTo("[1, 2, 3]");

        // 整个数组作为一个参数
        List<?> list = (List<?>) asList.invoke((Object) args);
        assertThat(list).hasSize(1).is(HamcrestCondition.matching(Is.is(new int[]{1, 2, 3})), Index.atIndex(0));
        list = ((List<?>) asListFix.invoke((Object) args));
        assertThat(list).hasSize(3).containsExactlyElementsOf((List) List.of(1, 2, 3));
    }

    @Test
    @SneakyThrows
    public void testDropArguments() {
        // String#substring()
        MethodType methodType = MethodType.methodType(String.class, int.class, int.class);
        MethodHandle substring = lookup.findVirtual(String.class, "substring", methodType);
        assertThat(substring.invoke("hello world", 6, 11)).isEqualTo("world");
        // 在参数 0 位置处添加 float、double 类型的两个参数
        MethodHandle newMh = MethodHandles.dropArguments(substring, 0, float.class, double.class);
        // 实际执行时会忽略添加的两个参数
        assertThat(newMh.invoke(0.5f, 2.33, "hello world", 6, 11)).isEqualTo("world");
    }

    @Test
    @SneakyThrows
    public void testInsertArguments() {
        // String#concat
        MethodType methodType = MethodType.methodType(String.class, String.class);
        MethodHandle concat = lookup.findVirtual(String.class, "concat", methodType);
        assertThat(concat.invoke("hello ", "world")).isEqualTo("hello world");
        // 设置参数 1 位置处的参数个给定的值
        MethodHandle newMh = MethodHandles.insertArguments(concat, 1, "!");
        // 因为已经设置了一个值，因此执行时只填一个值
        assertThat(newMh.invoke("hello world")).isEqualTo("hello world!");
    }

    @Test
    @SneakyThrows
    public void testFilterArguments() {
        MethodType methodType = MethodType.methodType(int.class, int.class, int.class);
        // 接收两个 int，返回最大的 int
        MethodHandle max = lookup.findStatic(Math.class, "max", methodType);
        // 返回字符串的长度
        MethodHandle length = lookup.findVirtual(String.class, "length", MethodType.methodType(int.class));
        // 对 max 的索引 1 及其以后的参数使用 length 进行预处理
        MethodHandle methodHandle = MethodHandles.filterArguments(max, 1, length);
        // 虽然传入的 hello world 是字符串，但是会使用 length 进行预处理，得到字符串的长度
        assertThat(methodHandle.invoke(1, "hello world")).isEqualTo(11);
    }

    static class FoldArgument {
        public static int getFirst(int a, int b, int c) {
            return a;
        }
    }

    @Test
    @SneakyThrows
    public void testFoldArguments() {
        MethodType methodType = MethodType.methodType(int.class, int.class, int.class);
        MethodHandle addExact = lookup.findStatic(Math.class, "addExact", methodType);
        MethodHandle getFirst = lookup.findStatic(FoldArgument.class, "getFirst",
                MethodType.methodType(int.class, int.class, int.class, int.class));
        // 对传入的参数按照 addExact 得到新值，然后添加到原参数最前面
        MethodHandle methodHandle = MethodHandles.foldArguments(getFirst, addExact);
        // 1 2 => 3 1 2
        assertThat(methodHandle.invoke(1, 2)).isEqualTo(3);
    }

    @Test
    @SneakyThrows
    public void testPermuteArguments() {
        MethodType methodType = MethodType.methodType(int.class, int.class, int.class);
        MethodHandle compare = lookup.findStatic(Integer.class, "compare", methodType);
        // 3 比 4 小，相比较时返回 -1
        assertThat(compare.invoke(3, 4)).isEqualTo(-1);
        // permute 即改变序列，下述操作将调用时的两个参数交换位置
        MethodHandle methodHandle = MethodHandles.permuteArguments(compare, methodType, 1, 0);
        // 参数会调换位置，因此相当于 invoke(4, 3)，因此返回 1
        assertThat(methodHandle.invoke(3, 4)).isEqualTo(1);
        // 也可以重复参数
        methodHandle = MethodHandles.permuteArguments(compare, methodType, 1, 1);
        // 虽然像是 3 与 4 比较，其实是 4 与 4 比较
        assertThat(methodHandle.invoke(3, 4)).isEqualTo(0);
    }

    static class CatchException {
        public int handleException(Exception e, String str) {
            System.out.println(str);
            return 0;
        }
    }

    @Test
    @SneakyThrows
    public void testCatchExceptions() {
        MethodType methodType = MethodType.methodType(int.class, String.class);
        MethodHandle parseInt = lookup.findStatic(Integer.class, "parseInt", methodType);
        assertThat(parseInt.invoke("212")).isEqualTo(212);
        // 如果传入 parseInt 的参数不能转换为整型数据，则会抛出异常，使用另一个方法句柄处理这个异常
        MethodType handleExceptionMt = MethodType.methodType(int.class, Exception.class, String.class);
        /*
         * 异常处理的方法句柄也有一定的要求：
         * 1. 该方法的返回值必须与原方法的返回值一样，第一个参数是处理的异常类型，其他参数依次与原方法对应
         * 2. 这里的异常处理方法是成员方法，因此在 invoke 是要首先传入一个对象，而这与原方法的参数列表类型不对应，因此需要使用
         *    bindTo() 方法，如果异常处理方法也是静态方法，则不存在这个问题。
         */
        MethodHandle handleException = lookup.findVirtual(CatchException.class, "handleException", handleExceptionMt)
                .bindTo(new CatchException());
        MethodHandle methodHandle = MethodHandles.catchException(parseInt, NumberFormatException.class, handleException);
        // 控制台还打印出 java
        assertThat(methodHandle.invoke("java")).isEqualTo(0);
    }

    static class GuardWithTest {
        public static boolean guardTest(int i) {
            return i > 10;
        }
    }

    @Test
    @SneakyThrows
    public void testGuardWithTest() {
        MethodHandle guardTest = lookup.findStatic(GuardWithTest.class, "guardTest",
                MethodType.methodType(boolean.class, int.class));
        MethodType methodType = MethodType.methodType(int.class, int.class, int.class);
        MethodHandle max = lookup.findStatic(Math.class, "max", methodType);
        MethodHandle min = lookup.findStatic(Math.class, "min", methodType);
        // 使用第一个方法句柄进行判断，条件满足时执行 max，反之执行 min
        MethodHandle test = guardTest.asType(guardTest.type().changeParameterType(0, Integer.class)).bindTo(1);
        /*
         * guardWithTest() 使用细节：
         * 1. 第一个参数的方法句柄必须返回基本类型 boolean，包装类 Boolean 也不行
         * 2. 第二个、第三个方法句柄的类型必须一致
         * 3. 如果第一个方法句柄对应的方法有参数，则需要使用 bindTo() 方法进行绑定，最终 invoke 传入的参数与
         *    第二个、第三个方法句柄的对应的方法参数一致
         */
        assertThat(MethodHandles.guardWithTest(test, max, min).invoke(1, 2))
                .isEqualTo(1);
    }

    @Test
    @SneakyThrows
    public void testFilterReturnValue() {
        MethodType substringMt = MethodType.methodType(String.class, int.class, int.class);
        MethodHandle substring = lookup.findVirtual(String.class, "substring", substringMt);

        MethodType toLowerCaseMt = MethodType.methodType(String.class);
        MethodHandle toLowerCase = lookup.findVirtual(String.class, "toUpperCase", toLowerCaseMt);

        // substring 执行得到的结果再使用 toLowerCase 执行
        MethodHandle methodHandle = MethodHandles.filterReturnValue(substring, toLowerCase);
        assertThat(methodHandle.invoke("hello world", 6, 11)).isEqualTo("WORLD");
    }

    @Test
    @SneakyThrows
    public void testExactInvoker() {
        MethodType typeInvoker = MethodType.methodType(String.class, String.class, int.class, int.class);
        MethodHandle invoker = MethodHandles.exactInvoker(typeInvoker);
        MethodType typeFind = MethodType.methodType(String.class, int.class, int.class);
        MethodHandle substring = lookup.findVirtual(String.class, "substring", typeFind);
        assertThat(invoker.invoke(substring, "hello world", 6, 11))
                .isEqualTo(substring.invoke("hello world", 6, 11))
                .isEqualTo("world");

        MethodHandle toUpperCase = lookup.findVirtual(
                String.class,
                "toUpperCase",
                MethodType.methodType(String.class)
        );
        MethodHandle methodHandle = MethodHandles.filterReturnValue(invoker, toUpperCase);
        // 对 invoker 创建的 MethodHandle 进行变换后，执行时这些变换会自动应用在传入的 MethodHandle 上
        assertThat((String) methodHandle.invokeExact(substring, "hello world", 6, 11)).isEqualTo("WORLD");
    }

    static class UseMethodHandleProxies {
        public String convert(Integer integer) {
            return String.valueOf(integer + 1);
        }
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public void testAsInterfaceInstance() {
        MethodType methodType = MethodType.methodType(String.class, Integer.class);
        MethodHandle convert = lookup.findVirtual(UseMethodHandleProxies.class, "convert", methodType);
        // 成员方法的 MethodHandle 在执行前需要绑定实例对象
        convert = convert.bindTo(new UseMethodHandleProxies());
        Function<Integer, String> function = MethodHandleProxies.asInterfaceInstance(Function.class, convert);
        assertThat(function.apply(3)).isEqualTo("4");
    }
}