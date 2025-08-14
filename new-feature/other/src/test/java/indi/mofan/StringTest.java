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
    public void testIndent() {
        String text = "java";
        // 末尾会自动追加换行
        assertThat(text.indent(4)).isEqualTo("    java\n");
        assertThat(text.indent(-4)).isEqualTo(text + "\n");

        // 前面有 4 个空格
        text = "    java";
        assertThat(text.indent(-2)).isEqualTo("  java\n");
        assertThat(text.indent(-5)).isEqualTo("java" + "\n");
    }

    @Test
    public void testTransform() {
        String transform = "hello".transform(i -> i + ", world");
        assertThat(transform).isEqualTo("hello, world");
    }

    @Test
    public void testFormatted() {
        String format = """
                1
                %s
                3
                """;
        String text = """
                1
                2
                3
                """;
        // 只要针对文本块使用
        assertThat(format.formatted("2")).isEqualTo(text);
    }

    @Test
    public void testTranslateEscapes() {
        String text = "Java\\nRust";
        assertThat(text.length()).isEqualTo(10);
        String block = """
                Java
                Rust""";
        assertThat(text.translateEscapes()).isEqualTo(block);
    }

    @Test
    public void testStringBlock() {
        String originBlock = """
                                \s\s\s\s<html>
                                    <body>
                                        <p>Hello, world</p>
                                    </body>
                                \s\s\s\s</html>""";

        String resultBlock = """
                            <html>
                            <body>
                                <p>Hello, world</p>
                            </body>
                            </html>""";
        // 整体左移，并保持相对缩进不变
        assertThat(originBlock.stripIndent()).isEqualTo(resultBlock);

        // 删除开始和结束的空格
        String text = "hello world";
        assertThat("   hello world".stripIndent()).isEqualTo(text);
        assertThat("hello world  ".stripIndent()).isEqualTo(text);
        assertThat("   hello world  ".stripIndent()).isEqualTo(text);
    }

    @Test
    public void testTextBlockEscapesChars() {
        String textBlock = """
                Hello \
                World\
                """;
        assertThat(textBlock).isEqualTo("Hello World");
    }
}
