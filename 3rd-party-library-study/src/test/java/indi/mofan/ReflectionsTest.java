package indi.mofan;


import indi.mofan.annotaion.AnnotationForConstructor;
import indi.mofan.annotaion.AnnotationForField;
import indi.mofan.annotaion.AnnotationForMethod;
import indi.mofan.annotaion.AnnotationForParameter;
import indi.mofan.annotaion.AnnotationForType;
import indi.mofan.entity.BaseEntity;
import indi.mofan.entity.UserInfo;
import org.junit.jupiter.api.Test;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.Store;
import org.reflections.scanners.MemberUsageScanner;
import org.reflections.scanners.MethodParameterNamesScanner;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.QueryFunction;
import org.reflections.util.ReflectionUtilsPredicates;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;


/**
 * @author mofan
 * @date 2021/3/20
 */
public class ReflectionsTest {

    private static String basePackageName = "indi.mofan";
    private static String packageName = "indi.mofan";

    @Test
    public void testAnnotation() {
        // 配置扫描的包
        Collection<URL> forPackage = ClasspathHelper.forPackage(packageName);
        // 需设置为 false，否则 getAllTypes() 方法将报错
        Scanners subTypesScanner = Scanners.SubTypes.filterResultsBy(s -> true);
        // 构建配置对象
        ConfigurationBuilder configuration = new ConfigurationBuilder().setUrls(forPackage)
                // 配置注解扫描器
                .setScanners(subTypesScanner, Scanners.TypesAnnotated, Scanners.MethodsAnnotated,
                        Scanners.FieldsAnnotated, Scanners.MethodsParameter);
        // 使用配置
        Reflections reflections = new Reflections(configuration);


        // 获取某个包下某个类的子类
        Set<Class<? extends BaseEntity>> subTypesOf = reflections.getSubTypesOf(BaseEntity.class);
        System.out.println(subTypesOf);
        System.out.println("==========================================================");
        // 获取所有 Object 类的子类，不推荐使用
        Set<String> allTypes = reflections.getAll(Scanners.SubTypes);
        allTypes.forEach(System.out::println);
        System.out.println("==========================================================");
        // 获取某个包下被某个注解注释的类
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(AnnotationForType.class, true);
        System.out.println(typesAnnotatedWith);
        // 获取某个包下被某个注解注释的方法
        Set<Method> methodsAnnotatedWith = reflections.getMethodsAnnotatedWith(AnnotationForMethod.class);
        System.out.println(methodsAnnotatedWith);
        // 获取某个包下被某个注解注释的构造方法
        Set<Constructor> constructorsAnnotatedWith = reflections.getConstructorsAnnotatedWith(AnnotationForConstructor.class);
        System.out.println(constructorsAnnotatedWith);
        // 获取某个包下被某个注解注释的字段
        Set<Field> fieldsAnnotatedWith = reflections.getFieldsAnnotatedWith(AnnotationForField.class);
        System.out.println(fieldsAnnotatedWith);
    }

    @Test
    public void testMethod() throws NoSuchMethodException {
        MethodParameterNamesScanner methodParameterNamesScanner = new MethodParameterNamesScanner();
        MemberUsageScanner memberUsageScanner = new MemberUsageScanner();
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage(packageName))
                        .setScanners(methodParameterNamesScanner, Scanners.MethodsParameter,
                                Scanners.MethodsSignature, Scanners.MethodsAnnotated, memberUsageScanner)
        );

        // 获取方法参数名
        Method setUsername = UserInfo.class.getMethod("setUsername", String.class);
        Set<String> methodParamNames = reflections.get(Scanners.MethodsParameter.with(setUsername));
        methodParamNames.forEach(System.out::println);
        System.out.println("==========================================================");
        // 获取指定参数类型的方法
        Set<Method> methodsMatchParams = reflections.getMethodsWithSignature(String.class);
        methodsMatchParams.forEach(System.out::println);
        System.out.println("==========================================================");
        // 获取指定返回值类型的方法
        Set<Method> methodsReturn = reflections.getMethodsReturn(String.class);
        methodsReturn.forEach(System.out::println);
        System.out.println("==========================================================");
        // 获取任何参数上带有指定注解的方法
        Set<Method> methodsWithAnyParamAnnotated = reflections.getMethodsAnnotatedWith(AnnotationForParameter.class);
        methodsWithAnyParamAnnotated.forEach(System.out::println);
        System.out.println("==========================================================");
        // 获取某个方法的被哪些方法使用了，不推荐使用 todo 新版本中没找到替代，先注释
//        Set<Member> methodUsage = reflections.getMethodUsage(UserInfo.class.getMethod("getUsername"));
//        methodUsage.forEach(System.out::println);
    }

    @Test
    public void testConstructor() {
        MethodParameterNamesScanner methodParameterNamesScanner = new MethodParameterNamesScanner();
        MemberUsageScanner memberUsageScanner = new MemberUsageScanner();
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage(packageName))
                        .setScanners(methodParameterNamesScanner, Scanners.MethodsParameter, memberUsageScanner)
        );
        // 获取指定参数类型的构造方法参数名
        QueryFunction<Store, String> constructParamNamesFunc = ReflectionUtils.Constructors.of(UserInfo.class)
                .filter(ReflectionUtilsPredicates.withParameters(String.class))
                .map(i -> Arrays.toString(i.getParameterTypes()));
        Set<String> constructorParamNames = reflections.get(constructParamNamesFunc);
        constructorParamNames.forEach(System.out::println);
        System.out.println("==========================================================");
        // 获取指定参数类型的构造方法
        Set<Constructor> constructorsMatchParams = reflections.getConstructorsWithSignature(String.class);
        constructorsMatchParams.forEach(System.out::println);
        System.out.println("==========================================================");
        // 获取任何参数上带有指定注解的构造方法
        Set<Constructor> constructorsWithAnyParamAnnotated =
                reflections.getConstructorsWithParameter(AnnotationForParameter.class);
        constructorsWithAnyParamAnnotated.forEach(System.out::println);
        // 获取某个构造方法的使用情况 todo 新版本中没找到替代，先注释
//        Constructor<UserInfo> userInfoConstructor = UserInfo.class.getConstructor(String.class);
//        Set<Member> constructorUsage = reflections.getConstructorUsage(userInfoConstructor);
//        constructorUsage.forEach(System.out::println);
    }

    @Test
    public void testResources() {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage(basePackageName))
                        .setScanners(Scanners.Resources)
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
