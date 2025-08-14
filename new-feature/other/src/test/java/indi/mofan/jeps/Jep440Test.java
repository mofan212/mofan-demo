package indi.mofan.jeps;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author mofan
 * @date 2023/11/14 23:03
 * @link <a href="https://openjdk.org/jeps/440">JEP 440: Record Patterns</a>
 */
public class Jep440Test implements WithAssertions {
    record Point(int x, int y) {
    }

    private int sum(Object obj) {
        if (obj instanceof Point(int x, int y)) {
            return x + y;
        }
        return Integer.MIN_VALUE;
    }

    /**
     * Pattern matching and records
     */
    @Test
    public void testSum() {
        int sum = sum(new Point(1, 2));
        assertThat(sum).isEqualTo(3);
        sum = sum(new Object());
        assertThat(sum).isEqualTo(Integer.MIN_VALUE);
    }

    enum Color {RED, GREEN, BLUE}

    record ColoredPoint(Point p, Color c) {
    }

    record Rectangle(ColoredPoint upperLeft, ColoredPoint lowerRight) {
    }

    private ColoredPoint getUpperLeftColoredPoint(Object obj) {
        if (obj instanceof Rectangle(ColoredPoint ul, ColoredPoint lr)) {
            return ul;
        }
        return null;
    }

    private Color getColorOfUpperLeftPoint(Object obj) {
        // 可以嵌套
        if (obj instanceof Rectangle(
                ColoredPoint(Point p, Color c),
                ColoredPoint lr
        )) {
            return c;
        }
        return null;
    }

    private int getXCoordOfUpperLeftPointWithPatterns(Object obj) {
        // 可以使用 var 关键字
        if (obj instanceof Rectangle(ColoredPoint(Point(var x, var y), var c), var lr)) {
            return x;
        }
        return Integer.MIN_VALUE;
    }

    @Test
    public void testNestedRecordPatterns() {
        ColoredPoint upperLeft = new ColoredPoint(new Point(1, 2), Color.GREEN);
        ColoredPoint lowerRight = new ColoredPoint(new Point(3, 4), Color.BLUE);
        Rectangle rectangle = new Rectangle(upperLeft, lowerRight);
        ColoredPoint ul = getUpperLeftColoredPoint(rectangle);
        assertThat(ul).isNotNull()
                .extracting(ColoredPoint::c)
                .isEqualTo(Color.GREEN);

        Color color = getColorOfUpperLeftPoint(rectangle);
        assertThat(color).isEqualTo(Color.GREEN);

        int x = getXCoordOfUpperLeftPointWithPatterns(rectangle);
        assertThat(x).isEqualTo(1);
    }

    record Pair(Object x, Object y) {
    }

    @Test
    public void testFailMatch() {
        Pair pair = new Pair(42, 42);
        // 类型匹配失败
        if (pair instanceof Pair(String s, String t)) {
            Assertions.fail();
        }
        // 类型匹配成功
        if (pair instanceof Pair(Integer s, Integer t)) {
            assertThat(s + t).isEqualTo(84);
        }
        // null 不与任何 record 匹配
        if (null instanceof Pair(var s, var t)) {
            Assertions.fail();
        }
    }

    record MyPair<S, T>(S fst, T snd) {
    }

    record Box<T>(T t) {
    }

    @Test
    public void testInferGeneric() {
        MyPair<Integer, Integer> pair = new MyPair<>(1, 2);
        if (pair instanceof MyPair(var x, var y)) {
            // x 与 y 被推断为 Integer
            assertThat(x + y).isEqualTo(3);
        }

        String str = "test";
        // 嵌套的泛型也能推断
        var box = new Box<>(new Box<>(str));
        if (box instanceof Box<Box<String>>(Box(var s))) {
            assertThat(s).isEqualTo(str);
        }
        // 外部的类型参数可以进一步简化
        if (box instanceof Box(Box(var s))) {
            assertThat(s).isEqualTo(str);
        }
    }
}
