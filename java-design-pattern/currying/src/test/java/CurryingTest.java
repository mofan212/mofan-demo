import indi.mofan.Book;
import indi.mofan.Genre;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

/**
 * @author mofan
 * @date 2024/3/10 23:37
 */
public class CurryingTest implements WithAssertions {
    @Test
    public void test() {
        // Defining genre book functions
        Book.AddAuthor fantasyBookFunc = Book.builder().withGenre(Genre.FANTASY);
        Book.AddAuthor horrorBookFunc = Book.builder().withGenre(Genre.HORROR);
        Book.AddAuthor scifiBookFunc = Book.builder().withGenre(Genre.SCI_FI);

        // Defining author book functions
        Book.AddTitle kingFantasyBooksFunc = fantasyBookFunc.withAuthor("Stephen King");
        Book.AddTitle kingHorrorBooksFunc = horrorBookFunc.withAuthor("Stephen King");
        Book.AddTitle rowlingFantasyBooksFunc = fantasyBookFunc.withAuthor("J.K. Rowling");

        // Creates books by Stephen King (horror and fantasy genres)
        Book shining = kingHorrorBooksFunc.withTitle("The Shining")
                .withPublicationDate(LocalDate.of(1977, 1, 28));
        Book darkTower = kingFantasyBooksFunc.withTitle("The Dark Tower: Gunslinger")
                .withPublicationDate(LocalDate.of(1982, 6, 10));

        // Creates fantasy books by J.K. Rowling
        Book chamberOfSecrets = rowlingFantasyBooksFunc.withTitle("Harry Potter and the Chamber of Secrets")
                .withPublicationDate(LocalDate.of(1998, 7, 2));

        // Create sci-fi books
        Book dune = scifiBookFunc.withAuthor("Frank Herbert")
                .withTitle("Dune")
                .withPublicationDate(LocalDate.of(1965, 8, 1));
        Book foundation = scifiBookFunc.withAuthor("Isaac Asimov")
                .withTitle("Foundation")
                .withPublicationDate(LocalDate.of(1942, 5, 1));

        assertThat(shining.toString()).isEqualTo("Book[genre=HORROR, author=Stephen King, title=The Shining, publicationDate=1977-01-28]");
        assertThat(darkTower.toString()).isEqualTo("Book[genre=FANTASY, author=Stephen King, title=The Dark Tower: Gunslinger, publicationDate=1982-06-10]");
        assertThat(chamberOfSecrets.toString()).isEqualTo("Book[genre=FANTASY, author=J.K. Rowling, title=Harry Potter and the Chamber of Secrets, publicationDate=1998-07-02]");
        assertThat(dune.toString()).isEqualTo("Book[genre=SCI_FI, author=Frank Herbert, title=Dune, publicationDate=1965-08-01]");
        assertThat(foundation.toString()).isEqualTo("Book[genre=SCI_FI, author=Isaac Asimov, title=Foundation, publicationDate=1942-05-01]");
    }
}
