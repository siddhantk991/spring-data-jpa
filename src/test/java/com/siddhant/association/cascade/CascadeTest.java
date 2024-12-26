package com.siddhant.association.cascade;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * From the javadoc of the @DataJpaTest annotation (spring boot 3.3.0):
 * "By default, tests annotated with @DataJpaTest are transactional and
 * roll back at the end of each test. They also use an embedded in-memory
 * database (replacing any explicit or usually auto-configured DataSource).
 * The @AutoConfigureTestDatabase annotation can be used to override these
 * settings
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CascadeTest {

    @Autowired
    private BookRepository customerRepo;

    @Autowired
    AuthorRepository orderRepo;

    private Author author;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;


    @PostConstruct
    void init() {
        author = new Author();
        Book book1 = new Book();
        Book book2 = new Book();
        List<Book> books = new ArrayList<>();
        books.add(book1);
        books.add(book2);
        author.setBooks(books);
        book1.setAuthor(author);
        book2.setAuthor(author);
    }

    @Test
    void testCascade() {
        // create an author with books using cascading all
        authorRepository.save(author);
        assertNotNull(author.getId());

        // get the created author from repository.
        Author author1 = authorRepository.getReferenceById(author.getId());
        // test author creation and its books creation with non-null check
        assertNotNull(author1);
        assertNotNull(author1.getId());

        Book book = bookRepository.getReferenceById(author.getBooks().getFirst().getId());
        assertNotNull(book);
        assertNotNull(book.getId());

        Long bookId = book.getId();

        // delete author in repository
        authorRepository.delete(author1);

        // test if author and its books deleted
        Optional<Author> optionalAuthor = authorRepository.findById(author.getId());
        assertFalse(optionalAuthor.isPresent());
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        assertFalse(optionalBook.isPresent());
        assertEquals(0, bookRepository.count());
    }
}
