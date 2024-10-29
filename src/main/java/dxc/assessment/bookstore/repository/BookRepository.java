package dxc.assessment.bookstore.repository;

import dxc.assessment.bookstore.model.Author;
import dxc.assessment.bookstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    // Custom query methods can be added here

    List<Book> findByTitle(String title);

    List<Book> findByAuthors_Name(String author);

    List<Book> findByTitleAndAuthors_Name(String title, String author);

    Optional<Book> findByIsbn(String isbn);
}