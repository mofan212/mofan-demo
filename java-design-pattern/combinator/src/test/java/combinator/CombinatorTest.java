package combinator;

import indi.mofan.find.Finder;
import indi.mofan.find.Finders;
import indi.mofan.user.User;
import indi.mofan.user.validate.UserValidation;
import indi.mofan.user.validate.ValidationResult;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author mofan
 * @date 2024/3/17 22:59
 */
public class CombinatorTest implements WithAssertions {

    static final String TEXT = """
            It was many and many a year ago,
            In a kingdom by the sea,
            That a maiden there lived whom you may know
            By the name of ANNABEL LEE;
            And this maiden she lived with no other thought
            Than to love and be loved by me.
            I was a child and she was a child,
            In this kingdom by the sea;
            But we loved with a love that was more than love-
            I and my Annabel Lee;
            With a love that the winged seraphs of heaven
            Coveted her and me.""";

    @Test
    public void testFinder() {
        String[] queriesOr = {"many", "Annabel"};
        Finder finder = Finders.expandedFinder(queriesOr);
        List<String> res = finder.find(TEXT);
        assertThat(res).containsExactly(
                "It was many and many a year ago,",
                "By the name of ANNABEL LEE;",
                "I and my Annabel Lee;"
        );

        String[] queriesAnd = {"Annabel", "my"};
        finder = Finders.specializedFinder(queriesAnd);
        res = finder.find(TEXT);
        assertThat(res).singleElement().isEqualTo("I and my Annabel Lee;");

        finder = Finders.advancedFinder("it was", "kingdom", "sea");
        res = finder.find(TEXT);
        assertThat(res).singleElement().isEqualTo("It was many and many a year ago,");

        finder = Finders.filteredFinder(" was ", "many", "child");
        res = finder.find(TEXT);
        assertThat(res).singleElement().isEqualTo("But we loved with a love that was more than love-");
    }

    @Test
    public void testValidateUser() {
        UserValidation validation = UserValidation.nameIsNotEmpty().and(UserValidation.eMailContainsAtSign());

        User emptyNameUser = new User("", 22, "mail@xx.com");
        ValidationResult result = validation.apply(emptyNameUser);
        assertThat(result.isValid()).isFalse();
        assertThat(result.getReason()).isPresent().hasValue("Name is empty.");
    }
}
