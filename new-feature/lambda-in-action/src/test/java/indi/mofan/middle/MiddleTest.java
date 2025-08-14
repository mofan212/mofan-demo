package indi.mofan.middle;


import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

/**
 * @author mofan
 * @date 2025/3/9 15:44
 */
public class MiddleTest implements WithAssertions {

    ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

    @Test
    public void testHigherOrderFunctionFactory() {
        HigherOrderFunctionFactory factory = context.getBean(HigherOrderFunctionFactory.class);
        String value = factory.execute(HigherOrderFunctionFactory.Type.ONE, "abc");
        assertThat(value).isEqualTo("ABC");
        value = factory.execute(HigherOrderFunctionFactory.Type.TWO, "XYZ");
        assertThat(value).isEqualTo("xyz");
        value = factory.execute(HigherOrderFunctionFactory.Type.THREE, " ");
        assertThat(value).isEmpty();
    }

    @Test
    public void testSwitchPatternMatch() {
        String value = HigherOrderFunctionFactory.run(HigherOrderFunctionFactory.Type.ONE).apply("abc");
        assertThat(value).isEqualTo("ABC");
        value = HigherOrderFunctionFactory.run(HigherOrderFunctionFactory.Type.TWO).apply("XYZ");
        assertThat(value).isEqualTo("xyz");
        value = HigherOrderFunctionFactory.run(HigherOrderFunctionFactory.Type.THREE).apply(" ");
        assertThat(value).isEmpty();
    }

    @Test
    public void testCreateBook() {
        Book.AddAuthor scifiBookFunc = Book.builder().withGenre(Genre.SCI_FI);

        Book dune = scifiBookFunc.withAuthor("Frank Herbert")
                .withTitle("Dune")
                .withPublicationDate(LocalDate.of(1965, 8, 1));
        Book foundation = scifiBookFunc.withAuthor("Isaac Asimov")
                .withTitle("Foundation")
                .withPublicationDate(LocalDate.of(1942, 5, 1));

        assertThat(dune).extracting(Book::title).isEqualTo("Dune");
        assertThat(foundation).extracting(Book::title).isEqualTo("Foundation");
    }
}
