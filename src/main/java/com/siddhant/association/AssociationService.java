package com.siddhant.association;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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

    @Transactional
    public void deleteViaIdentifiers(Long authorId) {
        bookRepository.deleteByAuthorIdentifier(authorId);
        authorRepository.deleteByIdentifier(authorId);
    }

    @Transactional
    public void deleteViaBulkIn(int authorAge) {
        List<Author> authors = authorRepository.findByAge(authorAge);
        bookRepository.deleteBulkByAuthors(authors);
        authorRepository.deleteAllInBatch(authors);
    }
    // delete in batch for one author and its associated books.
    @Transactional
    public void deleteViaDeleteInBatch(Long authorId) {
        Author author = authorRepository.getReferenceById(authorId);
        bookRepository.deleteAllInBatch(author.getBooks());
        authorRepository.deleteAllInBatch(List.of(author));
    }
}
