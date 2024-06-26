package indi.mofan;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.sql.Driver;
import java.util.ArrayList;

/**
 * 输出内置的 ClassLoader
 *
 * @author mofan
 * @date 2024/6/26 10:47
 */
public class BuiltinClassLoaderTest implements WithAssertions {
    @Test
    public void test() {
        // AppClassLoader 应用类加载器，加载 classpath 目录下的类
        assertThat(BuiltinClassLoaderTest.class.getClassLoader().toString())
                .contains("jdk.internal.loader.ClassLoaders$AppClassLoader");

        /*
         * Java 9 移除了拓展机制，ExtClassLoader 被 PlatformClassLoader 取代
         * PlatformClassLoader 主要用于加载 Java 平台模块中的类，包括 java.sql、java.xml 中的类
         */
        assertThat(Driver.class.getClassLoader().toString())
                .contains("jdk.internal.loader.ClassLoaders$PlatformClassLoader");

        // 为 null，即为 BootstrapClassLoader
        assertThat(ArrayList.class.getClassLoader()).isNull();
    }
}
