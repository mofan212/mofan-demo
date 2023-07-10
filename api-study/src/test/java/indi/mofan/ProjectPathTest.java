package indi.mofan;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;

/**
 * @author mofan
 * @date 2023/7/10 19:10
 */
public class ProjectPathTest {
    @Test
    @SneakyThrows
    public void test() {
        // 相对路径：xxx/mofan-demo/api-study
        String relativePath = System.getProperty("user.dir");

        // 以一个目标文件为定位点
        URL url = getClass().getClassLoader().getResource("test.txt");
        Assertions.assertNotNull(url);
        // xxx/mofan-demo/api-study/target/test-classes/test.txt
        String path = new File(url.toURI()).getPath();
        Assertions.assertEquals(relativePath + "\\target\\test-classes\\test.txt", path);
        // 文件前要加 /
        url = getClass().getResource("/test.txt");
        Assertions.assertNotNull(url);
        Assertions.assertEquals(path, new File(url.toURI()).getPath());

        url = getClass().getResource("");
        Assertions.assertNotNull(url);
        // 加上具体的包路径
        Assertions.assertEquals(relativePath + "\\target\\test-classes\\indi\\mofan", new File(url.toURI()).getPath());
        // 与 System.getProperty("user.dir") 的结果一样
        Assertions.assertEquals(relativePath, new File("").getCanonicalPath());
    }
}
