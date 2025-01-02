package com.siddhant.association;

import com.siddhant.DockerDataSourceInitializer;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
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
    @Autowired
    EntityManager em;

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

        // delete author in repository alongwith books with as much queries as this
        // author has books.
        authorRepository.delete(author1);

        // test if author and its books deleted
        Optional<Author> optionalAuthor = authorRepository.findById(author.getId());
        assertFalse(optionalAuthor.isPresent());
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        assertFalse(optionalBook.isPresent());
        assertEquals(0, bookRepository.count());
    }

    @Test
    void testDeleteViaIdentifiers() {
        Author author1 = associationService.saveAuthor(author);
        assertNotNull(author1.getId());

        Long authorId = author1.getId();
        Long bookId = author1.getBooks().getFirst().getId();
        // delete author and its books in single queries while author in persistence context
        associationService.deleteViaIdentifiers(authorId);
        assertFalse(authorRepository.findById(authorId).isPresent());
        assertFalse(bookRepository.findById(bookId).isPresent());
    }

    // delete in bulk while bulk authors are in persistence context but not associated books.
    @Test
    void testDeleteBulkByAuthor() {
        Author author1 = new Author();
        author1.setName("Test Author");
        author1.setAge(35);
        Book book1 = new Book();
        book1.setTitle("C++");
        book1.setAuthor(author1);
        author1.getBooks().add(book1);
        authorRepository.save(author);
        authorRepository.save(author1);

        Long authorId = author.getId();
        Long authorId1 = author1.getId();
        Long bookId = author.getBooks().getFirst().getId();
        Long bookId1 = author1.getBooks().getFirst().getId();
        associationService.deleteViaBulkIn(35);
        assertFalse(authorRepository.findById(authorId).isPresent());
        assertFalse(authorRepository.findById(authorId1).isPresent());
        assertFalse(bookRepository.findById(bookId).isPresent());
        assertFalse(bookRepository.findById(bookId1).isPresent());
    }

    @Test
    void testDeleteViaDeleteInBatch() {
        authorRepository.save(author);
        Long authorId = author.getId();
        assertNotNull(authorId);

        Long bookId = author.getBooks().getFirst().getId();
        // delete author and its books in single queries while author & its books in persistence context
        associationService.deleteViaDeleteInBatch(authorId);
        // without flush and clear the tests will fail, because entity remains in persistence context
        // in case of deleting in batches, we may override deleteAllInBatch for adding flushAndClear
        em.flush();
        em.clear();

        assertFalse(bookRepository.findById(bookId).isPresent());
        assertFalse(authorRepository.findById(authorId).isPresent());
    }

    @Test
    void testDeleteViaBulkHardcodedIdentifiers() {
        Author author1 = new Author();
        author1.setName("Test Author");
        author1.setAge(35);
        Book book1 = new Book();
        book1.setTitle("C++");
        book1.setAuthor(author1);
        author1.getBooks().add(book1);
        authorRepository.save(author);
        authorRepository.save(author1);

        Long authorId = author.getId();
        Long authorId1 = author1.getId();
        Long bookId = author.getBooks().getFirst().getId();
        Long bookId1 = author1.getBooks().getFirst().getId();

        associationService.deleteViaBulkHardCodedIdentifiers(List.of(authorId, authorId1));

        assertFalse(authorRepository.findById(authorId).isPresent());
        assertFalse(authorRepository.findById(authorId1).isPresent());
        assertFalse(bookRepository.findById(bookId).isPresent());
        assertFalse(bookRepository.findById(bookId1).isPresent());
    }
}
