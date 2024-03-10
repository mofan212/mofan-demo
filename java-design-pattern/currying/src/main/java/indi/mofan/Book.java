package indi.mofan;

import java.time.LocalDate;

/**
 * @author mofan
 * @date 2024/3/10 23:23
 */
public record Book(Genre genre, String author, String title, LocalDate publicationDate) {
    public static AddGenre builder() {
        return genre
                -> author
                -> title
                -> publicationDate
                -> new Book(genre, author, title, publicationDate);
    }

    public interface AddGenre {
        Book.AddAuthor withGenre(Genre genre);
    }

    public interface AddAuthor {
        Book.AddTitle withAuthor(String author);
    }

    public interface AddTitle {
        Book.AddPublicationDate withTitle(String title);
    }

    public interface AddPublicationDate {
        Book withPublicationDate(LocalDate publicationDate);
    }

}
