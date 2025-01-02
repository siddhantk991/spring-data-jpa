package com.siddhant.association;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AssociationService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    @Transactional
    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    @Transactional
    public void deleteBook(Book book) {
        bookRepository.delete(book);
    }

    @Transactional
    public void deleteAuthor(Author author) {
        authorRepository.delete(author);
    }

    // delete author and its books in single queries while author in persistence context
    // persistence context for author may not required here as we provided authorId itself.
    @Transactional
    public void deleteViaIdentifiers(Long authorId) {
        log.info("deleting author with id:{} ", authorId);
//        Author author = authorRepository.getReferenceById(authorId);
        bookRepository.deleteByAuthorIdentifier(authorId);
        authorRepository.deleteByIdentifier(authorId);
    }
    // delete in bulk while bulk authors are in persistence context but not associated books.
    // it should execute only 2 queries to delete 1 for author and 1 for books.
    // deleting author may have more than 1 query to delete base on no. of authors and batch settings.
    @Transactional
    public void deleteViaBulkIn(int authorAge) {
        List<Author> authors = authorRepository.findByAge(authorAge);
        log.info("deleting authors:{} ", authors);
        bookRepository.deleteBulkByAuthors(authors);
        authorRepository.deleteAllInBatch(authors);
    }
    // delete in batch while one author and its associated books are in persistence context.
    // it should execute only 2 queries to delete 1 for author and 1 for books.
    // deleting books may have more than 1 query to delete base on no. of books and batch settings.
    @Transactional
    public void deleteViaDeleteInBatch(Long authorId) {
        Author author = authorRepository.getReferenceById(authorId);
        List<Book> books = bookRepository.findByAuthor_Id(authorId);
        log.info("deleting author:{} ", author);
        log.info("deleting books:{} ", books);
        bookRepository.deleteAllInBatch(books);
        authorRepository.deleteAllInBatch(List.of(author));
    }
    // delete authors & its associated books when neither exists in persistence context
    // must be executed single queries for deleting author & books
    // may be heave operation depending upon size of books & authors.
    @Transactional
    public void deleteViaBulkHardCodedIdentifiers(List<Long> authorsIds) {
        log.info("deleting authors:{} ", authorsIds);
        bookRepository.deleteBulkByAuthorIdentifier(authorsIds);
        authorRepository.deleteBulkByIdentifier(authorsIds);
    }
}
