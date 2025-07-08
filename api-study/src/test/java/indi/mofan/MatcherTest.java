package indi.mofan;


import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mofan
 * @date 2025/7/8 10:53
 */
public class MatcherTest implements WithAssertions {

    static final String STR = "hello hello hello";
    static final Pattern PATTERN = Pattern.compile("hello");

    @Test
    public void testMatches() {
        Matcher matcher = PATTERN.matcher(STR);

        // 整个字符串是否能匹配模式
        assertThat(matcher.matches()).isFalse();
    }

    @Test
    public void testFind() {
        Matcher matcher = PATTERN.matcher(STR);

        // 字符串中是否存在匹配模式的子串
        assertThat(matcher.find()).isTrue();
        // find 匹配成功后，会记录匹配的位置
        int expectStart = 0, expectEnd = 5;
        assertThat(matcher.start()).isEqualTo(expectStart);
        assertThat(matcher.end()).isEqualTo(expectEnd);
        // 直接使用 substring 截取字符串，非常棒！
        assertThat(STR.substring(expectStart, expectEnd)).isEqualTo("hello");
        // 直接使用 group 更爽
        assertThat(matcher.group()).isEqualTo("hello");

        // 每调用一次 find，匹配的位置都会更新
        assertThat(matcher.find()).isTrue();
        expectStart = 6;
        expectEnd = 11;
        assertThat(matcher.start()).isEqualTo(expectStart);
        assertThat(matcher.end()).isEqualTo(expectEnd);
        assertThat(STR.substring(expectStart, expectEnd)).isEqualTo("hello");
        assertThat(matcher.group()).isEqualTo("hello");

        assertThat(matcher.find()).isTrue();
        expectStart = 12;
        expectEnd = 17;
        assertThat(matcher.start()).isEqualTo(expectStart);
        assertThat(matcher.end()).isEqualTo(expectEnd);
        assertThat(STR.substring(expectStart, expectEnd)).isEqualTo("hello");
        assertThat(matcher.group()).isEqualTo("hello");

        // 匹配完所有子串后，再调用 find，会返回 false
        assertThat(matcher.find()).isFalse();
        // 返回 false 再调用相关方法，会抛出异常
        // noinspection ResultOfMethodCallIgnored
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(matcher::start);
        // noinspection ResultOfMethodCallIgnored
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(matcher::end);
        // noinspection ResultOfMethodCallIgnored
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(matcher::group);
    }

    final static String TEXT = "Name: John, Age: 30; Name: Mofan, Age: 23;";
    final static Pattern TEXT_PATTERN = Pattern.compile("Name: (\\w+), Age: (\\d+)");

    /**
     * 1. 当正则表达式包含用圆括号 `()` 定义的捕获组时，group(int group) 方法可获取这些组匹配到的具体内容。
     *      - group(0): 整个正则表达式 **匹配到** 的完整字符串（无论是否有捕获组）
     *      - group(1): 第一个捕获组匹配的内容
     *      - group(2): 第二个捕获组匹配的内容，依此类推
     * 2. 如果正则表达式中没有定义指定索引的捕获组，会抛出 `IndexOutOfBoundsException`
     * 3. 必须在调用 `group()` 前执行匹配操作（如 `find()`, `matches()`, `lookingAt()`），否则抛出 `IllegalStateException`
     */
    @Test
    public void testCapturingGroup() {
        // 没有捕获组
        Matcher matcher = PATTERN.matcher(STR);
        assertThat(matcher.find()).isTrue();
        assertThat(matcher.groupCount()).isEqualTo(0);
        // 匹配到的完整字符串，而不是整个字符串
        assertThat(matcher.group(0)).isEqualTo("hello");

        // 有捕获组
        matcher = TEXT_PATTERN.matcher(TEXT);
        assertThat(matcher.matches()).isFalse();
        // 尽管 matcher 失败，groupCount 依旧能够计算
        assertThat(matcher.groupCount()).isEqualTo(2);

        // 有捕获组，用 find
        assertThat(matcher.find()).isTrue();
        assertThat(matcher.groupCount()).isEqualTo(2);
        assertThat(matcher.group(0)).isEqualTo("Name: John, Age: 30");
        assertThat(matcher.group(1)).isEqualTo("John");
        assertThat(matcher.start(1)).isEqualTo(6);
        assertThat(matcher.end(1)).isEqualTo(10);
        assertThat(matcher.group(2)).isEqualTo("30");
        assertThat(matcher.start(2)).isEqualTo(17);
        assertThat(matcher.end(2)).isEqualTo(19);

        // 再调用一次 find，匹配后面的
        assertThat(matcher.find()).isTrue();
        assertThat(matcher.groupCount()).isEqualTo(2);
        assertThat(matcher.group(0)).isEqualTo("Name: Mofan, Age: 23");
        assertThat(matcher.group(1)).isEqualTo("Mofan");
        assertThat(matcher.start(1)).isEqualTo(27);
        assertThat(matcher.end(1)).isEqualTo(32);
        assertThat(matcher.group(2)).isEqualTo("23");
        assertThat(matcher.start(2)).isEqualTo(39);
        assertThat(matcher.end(2)).isEqualTo(41);
    }
}
