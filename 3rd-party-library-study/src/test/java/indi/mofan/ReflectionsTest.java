package indi.mofan;


import indi.mofan.reflections.annotaion.AnnotationForConstructor;
import indi.mofan.reflections.annotaion.AnnotationForField;
import indi.mofan.reflections.annotaion.AnnotationForMethod;
import indi.mofan.reflections.annotaion.AnnotationForParameter;
import indi.mofan.reflections.annotaion.AnnotationForType;
import indi.mofan.reflections.entity.BaseEntity;
import indi.mofan.reflections.entity.UserInfo;
import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MemberUsageScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.MethodParameterNamesScanner;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
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
public class ReflectionsTest implements WithAssertions {

    private static String basePackageName = "indi.mofan";
    private static String packageName = "indi.mofan.reflections";

    @Test
    @SneakyThrows
    public void testAnnotation() {
        // 配置扫描的包 ClasspathHelper.forPackage 有坑，不要用！！ https://github.com/ronmamo/reflections/issues/178
//        Collection<URL> forPackage = ClasspathHelper.forPackage(packageName);
        // 需设置为 false，否则 getAllTypes() 方法将报错
        SubTypesScanner subTypesScanner = new SubTypesScanner(false);
        // 配置注解扫描器
        TypeAnnotationsScanner typeAnnotationsScanner = new TypeAnnotationsScanner();
        MethodAnnotationsScanner methodAnnotationsScanner = new MethodAnnotationsScanner();
        FieldAnnotationsScanner fieldAnnotationsScanner = new FieldAnnotationsScanner();
        MethodParameterScanner methodParameterScanner = new MethodParameterScanner();
        Scanner[] scanners = {subTypesScanner, typeAnnotationsScanner, methodAnnotationsScanner,
                fieldAnnotationsScanner, methodParameterScanner};
        // 使用配置
        Reflections reflections = new Reflections(packageName, scanners);


        // 获取某个包下某个类的子类
        Set<Class<? extends BaseEntity>> subTypesOf =
                reflections.getSubTypesOf(BaseEntity.class);
        assertThat(subTypesOf).containsOnly(UserInfo.class);
        // 获取所有 Object 类的子类，不推荐使用
        Set<String> allTypes = reflections.getAllTypes();
        List<String> resultTypes = Arrays.asList(
                AnnotationForField.class.getName(),
                AnnotationForParameter.class.getName(),
                AnnotationForConstructor.class.getName(),
                AnnotationForMethod.class.getName(),
                AnnotationForType.class.getName(),
                UserInfo.class.getName(),
                BaseEntity.class.getName()
        );
        assertThat(allTypes).containsAll(resultTypes);
        // 获取某个包下被某个注解注释的类
        Class<UserInfo> userInfoClass = UserInfo.class;
        Set<Class<?>> typesAnnotatedWith =
                reflections.getTypesAnnotatedWith(AnnotationForType.class, true);
        assertThat(typesAnnotatedWith).containsOnly(UserInfo.class);
        // 获取某个包下被某个注解注释的方法
        Set<Method> methodsAnnotatedWith =
                reflections.getMethodsAnnotatedWith(AnnotationForMethod.class);
        Method getUsername = userInfoClass.getMethod("getUsername");
        assertThat(methodsAnnotatedWith).containsOnly(getUsername);
        // 获取某个包下被某个注解注释的构造方法
        Set<Constructor> constructorsAnnotatedWith =
                reflections.getConstructorsAnnotatedWith(AnnotationForConstructor.class);
        Constructor<UserInfo> constructor = userInfoClass.getConstructor();
        assertThat(constructorsAnnotatedWith).containsOnly(constructor);
        // 获取某个包下被某个注解注释的字段
        Set<Field> fieldsAnnotatedWith =
                reflections.getFieldsAnnotatedWith(AnnotationForField.class);
        Field usernameField = userInfoClass.getDeclaredField("username");
        assertThat(fieldsAnnotatedWith).containsOnly(usernameField);
    }

    @Test
    @SneakyThrows
    public void testMethod() {
        MethodParameterNamesScanner methodParameterNamesScanner = new MethodParameterNamesScanner();
        MethodParameterScanner methodParameterScanner = new MethodParameterScanner();
        MemberUsageScanner memberUsageScanner = new MemberUsageScanner();
        Scanner[] scanners = {methodParameterNamesScanner, methodParameterScanner, memberUsageScanner};
        Reflections reflections = new Reflections(packageName, scanners);

        // 获取方法参数名
        Method setUsername = UserInfo.class.getMethod("setUsername", String.class);
        List<String> methodParamNames = reflections.getMethodParamNames(setUsername);
        assertThat(methodParamNames).containsOnly("username");
        // 获取指定参数类型的方法 ---> 如果不写，就是获取无参方法
        Set<Method> methodsMatchParams = reflections.getMethodsMatchParams(String.class);
        Class<UserInfo> userInfoClass = UserInfo.class;
        Method setGender = userInfoClass.getMethod("setGender", String.class);
        Method setPwd = userInfoClass.getMethod("setPwd", String.class);
        assertThat(methodsMatchParams).containsExactlyInAnyOrder(setUsername, setGender, setPwd);
        // 获取指定返回值类型的方法
        Set<Method> methodsReturn = reflections.getMethodsReturn(String.class);
        Method useMethod = userInfoClass.getMethod("useMethod");
        Method getGender = userInfoClass.getMethod("getGender");
        Method getUsername = userInfoClass.getMethod("getUsername");
        Method getPwd = userInfoClass.getMethod("getPwd");
        assertThat(methodsReturn).containsExactlyInAnyOrder(useMethod, getGender, getUsername, getPwd);
        // 获取任何参数上带有指定注解的方法
        Set<Method> methodsWithAnyParamAnnotated =
                reflections.getMethodsWithAnyParamAnnotated(AnnotationForParameter.class);
        assertThat(methodsWithAnyParamAnnotated).containsOnly(setGender);
        // 获取某个方法的被哪些方法使用了，不推荐使用
        Set<Member> methodUsage =
                reflections.getMethodUsage(UserInfo.class.getMethod("getUsername"));
        assertThat(methodUsage).containsOnly(useMethod);
    }

    @Test
    @SneakyThrows
    public void testConstructor() {
        MethodParameterNamesScanner methodParameterNamesScanner = new MethodParameterNamesScanner();
        MethodParameterScanner methodParameterScanner = new MethodParameterScanner();
        MemberUsageScanner memberUsageScanner = new MemberUsageScanner();
        Scanner[] scanners = {methodParameterNamesScanner, methodParameterScanner, memberUsageScanner};
        Reflections reflections = new Reflections(packageName, scanners);
        // 获取指定参数类型的构造方法参数名
        Constructor<UserInfo> constructor = UserInfo.class.getConstructor(String.class);
        List<String> constructorParamNames = reflections.getConstructorParamNames(constructor);
        assertThat(constructorParamNames).containsOnly("username");
        // 获取指定参数类型的构造方法
        Set<Constructor> constructorsMatchParams = reflections.getConstructorsMatchParams(String.class);
        assertThat(constructorsMatchParams).containsOnly(constructor);
        // 获取任何参数上带有指定注解的构造方法
        Set<Constructor> constructorsWithAnyParamAnnotated =
                reflections.getConstructorsWithAnyParamAnnotated(AnnotationForParameter.class);
        assertThat(constructorsWithAnyParamAnnotated).containsOnly(
                UserInfo.class.getConstructor(String.class, String.class, String.class)
        );
        // 获取某个构造方法的使用情况
        Set<Member> constructorUsage = reflections.getConstructorUsage(constructor);
        assertThat(constructorUsage).containsOnly(
                UserInfo.class.getConstructor(String.class, String.class)
        );
    }

    @Test
    public void testResources() {
        Reflections reflections = new Reflections(basePackageName, new ResourcesScanner());
        // 获取资源文件的相对路径，使用正则表达式进行匹配
        Set<String> properties =
                reflections.getResources(Pattern.compile(".*\\.properties"));
        assertThat(properties).contains("indi/mofan/my.properties");
    }

    @Test
    @SneakyThrows
    @SuppressWarnings({"unchecked", "varargs"})
    public void testReflectionUtils() {
        // 必须是 public 方法
        Predicate<Method> publicPredicate = ReflectionUtils.withModifier(Modifier.PUBLIC);
        // 有 get 前缀
        Predicate<Method> getPredicate = ReflectionUtils.withPrefix("get");
        // 参数个数为 0
        Predicate<Member> paramPredicate = ReflectionUtils.withParametersCount(0);
        Class<UserInfo> userInfoClass = UserInfo.class;
        Set<Method> methods = ReflectionUtils.getAllMethods(userInfoClass, publicPredicate, getPredicate, paramPredicate);
        assertThat(methods).contains(
                userInfoClass.getMethod("getGender"),
                userInfoClass.getMethod("getUsername"),
                userInfoClass.getMethod("getPwd")
        );

        // 参数必须是 Collection 及其子类
        Predicate<Member> paramsPredicate = ReflectionUtils.withParametersAssignableTo(Collection.class);
        // 返回类型是 boolean
        Predicate<Method> returnPredicate = ReflectionUtils.withReturnType(boolean.class);
        methods = ReflectionUtils.getAllMethods(LinkedList.class, paramsPredicate, returnPredicate);
        assertThat(methods).isNotEmpty()
                .allMatch(i -> i.getName().endsWith("All"))
                .hasSize(13);

        // 字段有注解 AnnotationForField（注解的 RetentionPolicy 必须是 RUNTIME！）
        Predicate<Field> annotationPredicate = ReflectionUtils.withAnnotation(AnnotationForField.class);
        // 字段类型是 CharSequence 及其子类
        Predicate<Field> typeAssignablePredicate = ReflectionUtils.withTypeAssignableTo(CharSequence.class);
        Set<Field> fields = ReflectionUtils.getAllFields(UserInfo.class, annotationPredicate, typeAssignablePredicate);
        assertThat(fields).isNotEmpty().map(Field::getName).containsOnly("username");
    }
}
