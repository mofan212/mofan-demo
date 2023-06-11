package indi.mofan;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author mofan
 * @date 2023/6/11 17:57
 */
public class TryCatchTest implements WithAssertions {
    @Test
    public void testTryResources() {
        try (
                // 在当前路径下的 target 目录下写文件
                FileWriter writer = new FileWriter("./target/test-1.txt");
                BufferedWriter bw = new BufferedWriter(writer)
        ) {
            bw.write("hello world");
        } catch (IOException e) {
            // do something
        }

        assertThat(new File("./target/test-1.txt"))
                .isFile()
                .content()
                .isEqualTo("hello world");
    }

    @Test
    public void testEnhancedTryResources() throws Exception {
        // 在当前路径下的 target 目录下写文件
        FileWriter writer = new FileWriter("./target/test-2.txt");
        BufferedWriter bw = new BufferedWriter(writer);
        try (writer; bw) {
            bw.write("hello java");
        } catch (IOException e) {
            // do something
        }

        assertThat(new File("./target/test-2.txt"))
                .isFile()
                .content()
                .isEqualTo("hello java");
    }
}
