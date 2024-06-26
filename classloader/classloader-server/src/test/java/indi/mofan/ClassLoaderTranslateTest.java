package indi.mofan;

import indi.mofan.classloaders.MyCommonClassLoader;
import indi.mofan.service.api.Service;
import indi.mofan.util.TestUtils;
import org.junit.jupiter.api.Test;

/**
 * <p>自定义类加载器，并测试类加载器的传递性</p>
 * <p>
 *     1. 执行 {@code mvn compile} 编译 app-service 模块
 *     2. 在磁盘上新建 common-sdk 文件夹
 *     3. 将编译后 target 文件夹下的 indi 整个目录拷贝到 common-sdk 目录下再运行
 * </p>
 *
 * @author mofan
 * @date 2024/6/26 10:48
 */
public class ClassLoaderTranslateTest {
    @Test
    public void test() throws Exception {
        if (!TestUtils.checkPathExists()) {
            return;
        }
        // 记得放在 MyCommonClassLoader 中 loadClass() 方法内的注释
        ClassLoader myLoader = new MyCommonClassLoader(TestUtils.COMMON_PATH);
        Class<?> serviceCls = Class.forName(
                "indi.mofan.app.CompanyService",
                false,
                myLoader);

        Service service = (Service) serviceCls.getDeclaredConstructor().newInstance();
        service.start();
    }

// 准备使用 MyCommonClassLoader 加载类：indi.mofan.app.CompanyService
// 准备使用 MyCommonClassLoader 加载类：indi.mofan.service.api.Service
// 准备使用 MyCommonClassLoader 加载类：java.lang.Object
// 准备使用 MyCommonClassLoader 加载类：java.lang.System
// 准备使用 MyCommonClassLoader 加载类：java.lang.String
// 准备使用 MyCommonClassLoader 加载类：java.lang.invoke.StringConcatFactory
// 准备使用 MyCommonClassLoader 加载类：java.io.PrintStream
// start Service[indi.mofan.app.CompanyService@7bb58ca3]
// 准备使用 MyCommonClassLoader 加载类：indi.mofan.app.entities.Employee
// 准备使用 MyCommonClassLoader 加载类：indi.mofan.app.entities.Manager
// 公司员工: employee{name='张三'}
// 公司经理: manager{name='李四'}
}
