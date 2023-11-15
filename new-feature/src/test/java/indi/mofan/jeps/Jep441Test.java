package indi.mofan.jeps;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;

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
}
