package indi.mofan;

import indi.mofan.classloaders.MyCommonClassLoader;
import indi.mofan.util.TestUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.sql.Driver;

/**
 * 数组类关联的类加载器
 *
 * @author mofan
 * @date 2024/6/26 10:39
 */
public class ArrayClassLoaderTest implements WithAssertions {
    @Test
    public void test() throws Exception {
        int[] ia = new int[0];
        Assertions.assertNull(ia.getClass().getClassLoader());

        String[] sa = new String[0];
        Assertions.assertNull(sa.getClass().getClassLoader());

        Driver[] la = new Driver[0];
        assertThat(la.getClass().getClassLoader().toString())
                .contains("jdk.internal.loader.ClassLoaders$PlatformClassLoader");

        ArrayClassLoaderTest[] aa = new ArrayClassLoaderTest[0];
        assertThat(aa.getClass().getClassLoader().toString())
                .contains("jdk.internal.loader.ClassLoaders$AppClassLoader");

        ClassLoader myLoader = new MyCommonClassLoader(TestUtils.COMMON_PATH);
        Class<?> serviceCls = Class.forName(
                "indi.mofan.app.CompanyService",
                true,
                myLoader);

        Object[] oa = (Object[]) Array.newInstance(serviceCls, 0);
        assertThat(oa.getClass().getClassLoader()).isEqualTo(myLoader);
    }
}
