package indi.mofan;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

/**
 * @author mofan
 * @date 2023/6/12 17:27
 */
public class StringTest implements WithAssertions {
    @Test
    public void testIsBlank() {
        String str1 = "";
        String str2 = " ";
        String str3 = " \n ";
        String str4 = " \t ";
        long count = Stream.of(str1, str2, str3, str4).filter(String::isBlank).count();
        assertThat(count).isEqualTo(4);
    }

    @Test
    public void testLines() {
        long count = "1\n2\n3\n".lines().count();
        assertThat(count).isEqualTo(3);
    }

    @Test
    public void testRepeat() {
        String str = "abc";
        assertThat(str.repeat(3)).isEqualTo("abcabcabc");
    }

    @Test
    public void testStrip() {
        String str = " 1 2 3 4 ";
        // 去除首尾空格
        assertThat(str.strip()).isEqualTo("1 2 3 4");
        // 去除首位空格
        assertThat(str.stripLeading()).isEqualTo("1 2 3 4 ");
        // 去除末尾空格
        assertThat(str.stripTrailing()).isEqualTo(" 1 2 3 4");
    }

    @Test
    public void testStringBlock() {
        String originBlock = """
                                    <html>
                                        <body>
                                            <p>Hello, world</p>
                                        </body>
                                    </html>
                            """;

        String resultBlock = """
                            <html>
                                <body>
                                    <p>Hello, world</p>
                                </body>
                            </html>
                            """;

        assertThat(originBlock.stripIndent()).isEqualTo(resultBlock);
    }
}
