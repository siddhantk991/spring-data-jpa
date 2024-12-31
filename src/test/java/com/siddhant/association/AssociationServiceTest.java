package com.siddhant.association;

import com.siddhant.DockerDataSourceInitializer;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ComponentScan(basePackages = {"com.siddhant"})
// Annotation for a JPA test that focuses only on JPA components.
@DataJpaTest
// Tells Spring not to replace the application default DataSource.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// Defines class-level metadata that is used to determine how to load and configure an ApplicationContext for integration tests.
public class AssociationServiceTest extends DockerDataSourceInitializer {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private AssociationService associationService;

    Author author;

    @PostConstruct
    void init() {
        author = new Author();
        author.setName("John Doe");
        author.setAge(35);
        Book book1 = new Book();
        book1.setTitle("Java");
        book1.setAuthor(author);
        Book book2 = new Book();
        book2.setAuthor(author);
        book2.setTitle("Spring");
        List<Book> books = new ArrayList<>();
        books.add(book1);
        books.add(book2);
        author.getBooks().addAll(books);
    }

    @Test
    void addAuthor() {
        Author author1 = associationService.saveAuthor(author);
        assertNotNull(author1.getId());

        Book book = bookRepository.getReferenceById(author.getBooks().getFirst().getId());
        assertNotNull(book);
        assertNotNull(book.getId());

        Long bookId = book.getId();

//         delete author in repository
        authorRepository.delete(author1);

        // test if author and its books deleted
        Optional<Author> optionalAuthor = authorRepository.findById(author.getId());
        assertFalse(optionalAuthor.isPresent());
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        assertFalse(optionalBook.isPresent());
        assertEquals(0, bookRepository.count());
    }

}
