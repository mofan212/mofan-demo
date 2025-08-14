package indi.mofan.jeps;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * @author mofan
 * @date 2023/11/15 17:38
 * @link <a href="https://openjdk.org/jeps/441">JEP 441: Pattern Matching for switch</a>
 */
public class Jep441Test implements WithAssertions {

    private String formatterPatternSwitch(Object obj) {
        return switch (obj) {
            case Integer i -> String.format("int %d", i);
            case Long l -> String.format("long %d", l);
            case Double d -> String.format("double %f", d);
            case String s -> String.format("String %s", s);
            default -> obj.toString();
        };
    }

    @Test
    public void testFormatterPatternSwitch() {
        assertThat(formatterPatternSwitch(1)).startsWith("int 1");
        assertThat(formatterPatternSwitch(2L)).startsWith("long 2");
        assertThat(formatterPatternSwitch(2.2)).startsWith("double 2.2");
        assertThat(formatterPatternSwitch("test")).startsWith("String test");
        assertThat(formatterPatternSwitch(2.12f)).startsWith("2.12");
    }

    private String processFooBar(String s) {
        return switch (s) {
            case null -> "Oops";
            case "Foo", "Bar" -> "Great";
            default -> "Ok";
        };
    }

    @Test
    public void testNull() {
        assertThat(processFooBar(null)).isEqualTo("Oops");
        assertThat(processFooBar("Foo")).isEqualTo("Great");
        assertThat(processFooBar("Bar")).isEqualTo("Great");
        assertThat(processFooBar("default")).isEqualTo("Ok");
    }

    private String getStringEnhanced(String s) {
        return switch (s) {
            case null -> "Ok";
            case "y", "Y" -> "You got it";
            case "n", "N" -> "Shame";
            case String str when str.equalsIgnoreCase("YES") -> "You got it";
            case String str when str.equalsIgnoreCase("NO") -> "Shame";
            case String ignored -> "Sorry?";
        };
    }

    @Test
    public void testCaseRefinement() {
        assertThat(getStringEnhanced(null)).isEqualTo("Ok");
        for (String string : List.of("y", "Y", "YES", "yes", "Yes")) {
            assertThat(getStringEnhanced(string)).isEqualTo("You got it");
        }
        for (String string : List.of("n", "N", "NO", "No", "no")) {
            assertThat(getStringEnhanced(string)).isEqualTo("Shame");
        }
        assertThat(getStringEnhanced("A")).isEqualTo("Sorry?");
    }

    sealed interface CardClassification permits Suit, Tarot {
    }

    public enum Suit implements CardClassification {CLUBS, DIAMONDS, HEARTS, SPADES}

    static final class Tarot implements CardClassification {
    }

    private String exhaustiveSwitchWithBetterEnumSupport(CardClassification c) {
        return switch (c) {
            case Suit.CLUBS -> "C";
            case Suit.DIAMONDS -> "D";
            case Suit.HEARTS -> "H";
            case Suit.SPADES -> "S";
            case Tarot t -> "Tarot";
        };
    }

    @Test
    public void testSwitchesAndEnumConstants() {
        assertThat(exhaustiveSwitchWithBetterEnumSupport(new Tarot())).isEqualTo("Tarot");
        assertThat(exhaustiveSwitchWithBetterEnumSupport(Suit.CLUBS)).isEqualTo("C");
        assertThat(exhaustiveSwitchWithBetterEnumSupport(Suit.DIAMONDS)).isEqualTo("D");
        assertThat(exhaustiveSwitchWithBetterEnumSupport(Suit.HEARTS)).isEqualTo("H");
        assertThat(exhaustiveSwitchWithBetterEnumSupport(Suit.SPADES)).isEqualTo("S");
    }

    sealed interface Currency permits Coin {
    }

    enum Coin implements Currency {HEADS, TAILS}

    private String improveEnumConstant1(Currency c) {
        // 传入的不是枚举，而是父接口，需要指明具体的枚举
        return switch (c) {
            case Coin.HEADS -> "H";
            case Coin.TAILS -> "T";
        };
    }

    private String improveEnumConstant2(Coin c) {
        // 如果传入的是枚举，可以直接使用枚举项
        return switch (c) {
            case HEADS -> "H";
            case TAILS -> "T";
        };
    }

    @Test
    public void testImproveEnumConstant() {
        assertThat(improveEnumConstant1(Coin.HEADS)).isEqualTo("H");
        assertThat(improveEnumConstant1(Coin.TAILS)).isEqualTo("T");

        assertThat(improveEnumConstant2(Coin.HEADS)).isEqualTo("H");
        assertThat(improveEnumConstant2(Coin.TAILS)).isEqualTo("T");
    }

    private String patternsInSwitchLabels(Object obj) {
        return switch (obj) {
            // 先匹配长度为 1 的字符串
            case String s when s.length() == 1 -> "ONE";
            // 再匹配其他长度的字符串
            case String s -> "OTHER";
            // 默认匹配方式
            default -> "DEFAULT";
        };
    }

    @Test
    public void testPatternsInSwitchLabels() {
        assertThat(patternsInSwitchLabels("1")).isEqualTo("ONE");
        assertThat(patternsInSwitchLabels("test")).isEqualTo("OTHER");
        assertThat(patternsInSwitchLabels(1)).isEqualTo("DEFAULT");
    }

    private String dominanceOfCaseLabels1(Object obj) {
        // 和 try-catch 类似，前面 case 的范围不能包含后面
        return switch (obj) {
            case String s -> "String";
            case CharSequence c -> "Chars";
            default -> "Other";
        };
    }

    private String dominanceOfCaseLabels2(Integer integer) {
        return switch (integer) {
            case -1, 1 -> "First";
            // 大于 0，但又不是 -1 和 1
            case Integer i when i > 0 -> "Positive";
            case Integer i -> "Not Positive";
        };
    }

    @Test
    public void testDominanceOfCaseLabels() {
        assertThat(dominanceOfCaseLabels1("test")).isEqualTo("String");
        assertThat(dominanceOfCaseLabels1(new StringBuilder())).isEqualTo("Chars");
        assertThat(dominanceOfCaseLabels1(1)).isEqualTo("Other");

        for (Integer i : List.of(1, -1)) {
            assertThat(dominanceOfCaseLabels2(i)).isEqualTo("First");
        }

        IntStream.generate(() -> ThreadLocalRandom.current().nextInt())
                .filter(i -> i > 0 && i != 1)
                .limit(5)
                .forEach(i -> assertThat(dominanceOfCaseLabels2(i)).isEqualTo("Positive"));

        IntStream.generate(() -> ThreadLocalRandom.current().nextInt())
                .filter(i -> i <= 0 && i != -1)
                .limit(5)
                .forEach(i -> assertThat(dominanceOfCaseLabels2(i)).isEqualTo("Not Positive"));
    }

    sealed interface S permits A, B, C {
    }

    static final class A implements S {
    }

    static final class B implements S {
    }

    record C(int i) implements S {
    }

    private String sealedClass1(S s) {
        return switch (s) {
            case A a -> "A";
            case B b -> "B";
            case C(int i) -> String.valueOf(i);
        };
    }

    sealed interface I1<T> permits A1, B1 {
    }

    static final class A1<X> implements I1<String> {
    }

    static final class B1<Y> implements I1<Y> {
    }

    private String sealedClass2(I1<Integer> i1) {
        /*
         * i1 限定了泛型是 Integer
         * A1 虽然实现了 I1，但是其 I1 的泛型是 String，永远不会匹配 I1<Integer>
         */
        return switch (i1) {
            case B1<Integer> bi -> "212";
        };
    }

    @Test
    public void testExhaustivenessAndSealedClass() {
        assertThat(sealedClass1(new A())).isEqualTo("A");
        assertThat(sealedClass1(new B())).isEqualTo("B");
        assertThat(sealedClass1(new C(100))).isEqualTo("100");

        assertThat(sealedClass2(new B1<>())).isEqualTo("212");
    }

    private String dealingWithNull1(Object obj) {
        return switch (obj) {
            // 显示处理 null，不会抛出空指针异常
            case null -> "null";
            case String s -> s;
            default -> "Something else";
        };
    }

    private String dealingWithNull2(Object obj) {
        // 没有显示处理 null，如果传入 null，将抛出空指针
        // 相当于 case null -> throw new NullPointerException();
        return switch (obj) {
            case String s -> s;
            case Integer i -> String.valueOf(i);
            default -> "default";
        };
    }

    private String dealingWithNull3(Object obj) {
        return switch (obj) {
            case String s -> s;
            // null 和 default 可以一起使用
            // 但此时不能有单独的 case null
            case null, default -> "null or default";
        };
    }

    @Test
    @SuppressWarnings("all")
    public void testDealingWithNull() {
        assertThat(dealingWithNull1(null)).isEqualTo("null");
        assertThat(dealingWithNull1("test")).isEqualTo("test");
        assertThat(dealingWithNull1(1)).isEqualTo("Something else");

        assertThat(dealingWithNull2("test")).isEqualTo("test");
        assertThat(dealingWithNull2(2)).isEqualTo("2");
        assertThat(dealingWithNull2(2.12)).isEqualTo("default");
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> dealingWithNull2(null));

        assertThat(dealingWithNull3("test")).isEqualTo("test");
        assertThat(dealingWithNull3(1)).isEqualTo("null or default");
        assertThat(dealingWithNull3(null)).isEqualTo("null or default");
    }

    record R(int i) {
        @SuppressWarnings("divzero")
        public int i() {
            return this.i / 0;
        }
    }

    @SuppressWarnings("all")
    private String throwMatchException(R r) {
        return switch (r) {
            case R(var i) -> String.valueOf(i);
        };
    }

    @SuppressWarnings("all")
    private String throwArithmeticException(Object obj) {
        return switch (obj) {
            case R r when (r.i / 0 == 1) -> "R";
            default -> "";
        };
    }

    @Test
    @SuppressWarnings("all")
    public void testError() {
        assertThatExceptionOfType(MatchException.class)
                .isThrownBy(() -> throwMatchException(new R(1)));

        assertThatExceptionOfType(ArithmeticException.class)
                .isThrownBy(() -> throwArithmeticException(new R(1)));
        assertThat(throwArithmeticException(212)).isNotNull().isEmpty();
    }
}
