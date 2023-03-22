package indi.mofan;


import indi.mofan.reflections.annotaion.AnnotationForConstructor;
import indi.mofan.reflections.annotaion.AnnotationForField;
import indi.mofan.reflections.annotaion.AnnotationForMethod;
import indi.mofan.reflections.annotaion.AnnotationForParameter;
import indi.mofan.reflections.annotaion.AnnotationForType;
import indi.mofan.reflections.entity.BaseEntity;
import indi.mofan.reflections.entity.UserInfo;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MemberUsageScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterNamesScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;


/**
 * @author mofan
 * @date 2021/3/20
 */
public class ReflectionsTest {

    private static String basePackageName = "indi.mofan";
    private static String packageName = "indi.mofan.reflections";

    @Test
    public void testAnnotation() {
        // 配置扫描的包
        Collection<URL> forPackage = ClasspathHelper.forPackage(packageName);
        // 需设置为 false，否则 getAllTypes() 方法将报错
        SubTypesScanner subTypesScanner = new SubTypesScanner(false);
        // 配置注解扫描器
        TypeAnnotationsScanner typeAnnotationsScanner = new TypeAnnotationsScanner();
        MethodAnnotationsScanner methodAnnotationsScanner = new MethodAnnotationsScanner();
        FieldAnnotationsScanner fieldAnnotationsScanner = new FieldAnnotationsScanner();
        MethodParameterScanner methodParameterScanner = new MethodParameterScanner();
        // 构建配置对象
        ConfigurationBuilder configuration = new ConfigurationBuilder().setUrls(forPackage)
                .setScanners(subTypesScanner, typeAnnotationsScanner, methodAnnotationsScanner,
                        fieldAnnotationsScanner, methodParameterScanner);
        // 使用配置
        Reflections reflections = new Reflections(configuration);


        // 获取某个包下某个类的子类
        Set<Class<? extends BaseEntity>> subTypesOf =
                reflections.getSubTypesOf(BaseEntity.class);
        System.out.println(subTypesOf);
        System.out.println("==========================================================");
        // 获取所有 Object 类的子类，不推荐使用
        Set<String> allTypes = reflections.getAllTypes();
        allTypes.forEach(System.out::println);
        System.out.println("==========================================================");
        // 获取某个包下被某个注解注释的类
        Set<Class<?>> typesAnnotatedWith =
                reflections.getTypesAnnotatedWith(AnnotationForType.class, true);
        System.out.println(typesAnnotatedWith);
        // 获取某个包下被某个注解注释的方法
        Set<Method> methodsAnnotatedWith =
                reflections.getMethodsAnnotatedWith(AnnotationForMethod.class);
        System.out.println(methodsAnnotatedWith);
        // 获取某个包下被某个注解注释的构造方法
        Set<Constructor> constructorsAnnotatedWith =
                reflections.getConstructorsAnnotatedWith(AnnotationForConstructor.class);
        System.out.println(constructorsAnnotatedWith);
        // 获取某个包下被某个注解注释的字段
        Set<Field> fieldsAnnotatedWith =
                reflections.getFieldsAnnotatedWith(AnnotationForField.class);
        System.out.println(fieldsAnnotatedWith);
    }

    @Test
    @SneakyThrows
    public void testMethod() {
        MethodParameterNamesScanner methodParameterNamesScanner = new MethodParameterNamesScanner();
        MethodParameterScanner methodParameterScanner = new MethodParameterScanner();
        MemberUsageScanner memberUsageScanner = new MemberUsageScanner();
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage(packageName))
                        .setScanners(methodParameterNamesScanner, methodParameterScanner, memberUsageScanner)
        );

        // 获取方法参数名
        Method setUsername = UserInfo.class.getMethod("setUsername", String.class);
        List<String> methodParamNames = reflections.getMethodParamNames(setUsername);
        methodParamNames.forEach(System.out::println);
        System.out.println("==========================================================");
        // 获取指定参数类型的方法 ---> 如果不写，就是获取无参方法
        Set<Method> methodsMatchParams = reflections.getMethodsMatchParams(String.class);
        methodsMatchParams.forEach(System.out::println);
        System.out.println("==========================================================");
        // 获取指定返回值类型的方法
        Set<Method> methodsReturn = reflections.getMethodsReturn(String.class);
        methodsReturn.forEach(System.out::println);
        System.out.println("==========================================================");
        // 获取任何参数上带有指定注解的方法
        Set<Method> methodsWithAnyParamAnnotated =
                reflections.getMethodsWithAnyParamAnnotated(AnnotationForParameter.class);
        methodsWithAnyParamAnnotated.forEach(System.out::println);
        System.out.println("==========================================================");
        // 获取某个方法的被哪些方法使用了，不推荐使用
        Set<Member> methodUsage =
                reflections.getMethodUsage(UserInfo.class.getMethod("getUsername"));
        methodUsage.forEach(System.out::println);
    }

    @Test
    @SneakyThrows
    public void testConstructor() {
        MethodParameterNamesScanner methodParameterNamesScanner = new MethodParameterNamesScanner();
        MethodParameterScanner methodParameterScanner = new MethodParameterScanner();
        MemberUsageScanner memberUsageScanner = new MemberUsageScanner();
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage(packageName))
                        .setScanners(methodParameterNamesScanner, methodParameterScanner, memberUsageScanner)
        );
        // 获取指定参数类型的构造方法参数名
        Constructor<UserInfo> constructor = UserInfo.class.getConstructor(String.class);
        List<String> constructorParamNames = reflections.getConstructorParamNames(constructor);
        constructorParamNames.forEach(System.out::println);
        System.out.println("==========================================================");
        // 获取指定参数类型的构造方法
        Set<Constructor> constructorsMatchParams = reflections.getConstructorsMatchParams(String.class);
        constructorsMatchParams.forEach(System.out::println);
        System.out.println("==========================================================");
        // 获取任何参数上带有指定注解的构造方法
        Set<Constructor> constructorsWithAnyParamAnnotated =
                reflections.getConstructorsWithAnyParamAnnotated(AnnotationForParameter.class);
        constructorsWithAnyParamAnnotated.forEach(System.out::println);
        // 获取某个构造方法的使用情况
        Constructor<UserInfo> userInfoConstructor = UserInfo.class.getConstructor(String.class);
        Set<Member> constructorUsage = reflections.getConstructorUsage(userInfoConstructor);
        constructorUsage.forEach(System.out::println);
    }

    @Test
    public void testResources() {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage(basePackageName))
                        .setScanners(new ResourcesScanner())
        );
        // 获取资源文件的相对路径，使用正则表达式进行匹配
        Set<String> properties =
                reflections.getResources(Pattern.compile(".*\\.properties"));
        properties.forEach(System.out::println);
    }

    @Test
    @SuppressWarnings({"unchecked", "varargs"})
    public void testReflectionUtils() {
        // 必须是 public 方法
        Predicate<Method> publicPredicate = ReflectionUtils.withModifier(Modifier.PUBLIC);
        // 有 get 前缀
        Predicate<Method> getPredicate = ReflectionUtils.withPrefix("get");
        // 参数个数为 0
        Predicate<Member> paramPredicate = ReflectionUtils.withParametersCount(0);
        Set<Method> methods = ReflectionUtils.getAllMethods(UserInfo.class, publicPredicate, getPredicate, paramPredicate);
        methods.forEach(method -> System.out.println(method.getName()));
        System.out.println("==========================================================");

        // 参数必须是 Collection 及其子类
        Predicate<Member> paramsPredicate = ReflectionUtils.withParametersAssignableTo(Collection.class);
        // 返回类型是 boolean
        Predicate<Method> returnPredicate = ReflectionUtils.withReturnType(boolean.class);
        methods = ReflectionUtils.getAllMethods(LinkedList.class, paramsPredicate, returnPredicate);
        methods.forEach(method -> System.out.println(method.getName()));
        System.out.println("==========================================================");

        // 字段有注解 Native
//        Predicate<Field> annotationPredicate = ReflectionUtils.withAnnotation(Native.class);
        // 字段类型是int及其子类
        Predicate<Field> typeAssignablePredicate = ReflectionUtils.withTypeAssignableTo(int.class);
//        Set<Field> fields = ReflectionUtils.getAllFields(Integer.class, annotationPredicate, typeAssignablePredicate);
//        Set<Field> fields = ReflectionUtils.getAllFields(Integer.class, annotationPredicate);
        Set<Field> fields = ReflectionUtils.getAllFields(Integer.class, typeAssignablePredicate);
        fields.forEach(field -> System.out.println(field.getName()));
    }
}
