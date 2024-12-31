package com.siddhant;

import com.siddhant.association.Author;
import com.siddhant.association.AuthorRepository;
import com.siddhant.association.Book;
import com.siddhant.association.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
@Slf4j
public class SpringDataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDataJpaApplication.class, args);
	}

	/**
	 * This bean is created for testing purpose & on test environment only.
	 * We can visualize data using below command :
	 * Spring runs CommandLineRunner bean when Spring Boot App starts
	 * command: .\mvnw spring-boot:run
	 * db records added through this command is reverted automatically.
	 */
/**	@Bean
	public CommandLineRunner demo(BookRepository bookRepository, AuthorRepository authorRepository) {
		return (args) -> {

			Book b1 = new Book();
			b1.setTitle("Java");
			Book b2 = new Book();
			b2.setTitle("Spring");
			Book b3 = new Book();
			b3.setTitle("Hibernate");
			Book b4 = new Book();
			b4.setTitle("C++");

			Author author = new Author();
			author.setName("John Doe");
			author.setAge(35);
			author.addBook(b1);
			author.addBook(b2);
			author.addBook(b3);
			author.addBook(b4);
			authorRepository.save(author);

			Long authorId = author.getId();

			// get author by id calling db using findById()
			Optional<Author> author1 = authorRepository.findById(authorId);
			author1.ifPresent(auth-> {
				log.info("found author:{}", auth);
			});
			log.info("-------------------------------------");

			// find books by author-id
			log.info("find book by author_id {}", authorId);
			log.info("-------------------------------");
			for (Book book : bookRepository.findByAuthor_Id(authorId)) {
				log.info(book.toString());
			}
			log.info("\n");


			// find book by title
			log.info("Book found with findByTitle('Java')");
			log.info("--------------------------------------------");
			bookRepository.findByTitle("Java").forEach(b -> {
				log.info(b.toString());
				log.info("\n");
			});

			log.info("books count after saving author:{}", bookRepository.count());

			author.removeBook(b1);
			authorRepository.save(author);

			// check if orphan-removal works or not.
			log.info("size of books now:{}", bookRepository.count());

			authorRepository.deleteById(authorId);

			// check if author exists in db.
			Optional<Author> optionalAuthor = authorRepository.findById(authorId);
			log.info("author found with findById(authorId):{}", optionalAuthor.isPresent());
			log.info("--------------------------------------------");
			// find all books
			log.info("findAll() books with respect to author");
			log.info("-------------------------------");
			List<Book> books = bookRepository.findByAuthor_Id(authorId);
			log.info("books size:{} after deleting author authorId:{}", books.size(), authorId);
			log.info("\n");

		};
	} */

}
