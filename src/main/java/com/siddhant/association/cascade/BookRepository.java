package com.siddhant.association.cascade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByAuthor_Id(Long authorId);

    List<Book> findByTitle(String title);
}
