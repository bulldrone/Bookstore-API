package dxc.assessment.bookstore.service;

import dxc.assessment.bookstore.error.CustomExceptions;
import dxc.assessment.bookstore.model.Author;
import dxc.assessment.bookstore.model.Book;
import dxc.assessment.bookstore.repository.AuthorRepository;
import dxc.assessment.bookstore.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    public Book addBook(Book book) {
        Optional<Book> optionalBook = Optional.ofNullable(book);

        if(optionalBook.isPresent()) {
            bookRepository.findByIsbn(optionalBook.get().getIsbn()).ifPresentOrElse(
                    foundBook -> {
                        throw new CustomExceptions.InternalServerError("Book with ISBN already exists: " + foundBook.getIsbn());
                    },
                    () -> {
                        try {
                            List<Author> updatedAuthors = addOrRetrieveAuthors(book.getAuthors());
                            book.setAuthors(updatedAuthors);
                            bookRepository.save(book);
                        } catch (RuntimeException e) {
                            throw new CustomExceptions.InternalServerError(
                                    "Add Book fail! Book with " + book.getTitle() + " has experienced an internal server error during runtime."
                            );
                        }
                    }
            );
            return book;
        } else {
            throw new CustomExceptions.MissingJsonContentError("Missing body or wrong JSON format for book");
        }
    }

    public Book updateBook(String isbn, Book bookDetails) {
        Optional<Book> optionalBook = bookRepository.findByIsbn(isbn);

        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setTitle(bookDetails.getTitle());
            List<Author> updatedAuthors = addOrRetrieveAuthors(bookDetails.getAuthors());
            book.setAuthors(updatedAuthors);
            book.setPublishYear(bookDetails.getPublishYear());
            book.setPrice(bookDetails.getPrice());
            book.setGenre(bookDetails.getGenre());
            return bookRepository.save(book);
        } else {
            throw new CustomExceptions.BookNotFoundError("Update book fail! Book not found with ISBN: " + isbn);
        }
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll(); // Fetch all books from the repository
    }

    public Book getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn).orElseThrow(() ->
                new CustomExceptions.BookNotFoundError("Book not found with ISBN: " + isbn));
    }

    // Find books by title
    public List<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    public List<Book> findByAuthorsContaining(String author) {
        return bookRepository.findByAuthors_Name(author);
    }

    public List<Book> findByTitleAndAuthorsContaining(String title, String author) {
        return bookRepository.findByTitleAndAuthors_Name(title, author);
    }

    public ResponseEntity<Void> deleteBook(String isbn) {
        Optional<Book> optionalBook = bookRepository.findByIsbn(isbn);
        if (optionalBook.isPresent()) {
            try {
                bookRepository.delete(optionalBook.get());
                return ResponseEntity.noContent().build();
            } catch (Exception e) {
                // Throw custom BookDeletionException if delete fails
                throw new CustomExceptions.InternalServerError("Failed to delete the book with ISBN: " + isbn + ". Error:" + e.getMessage());
            }
        } else {
            throw new CustomExceptions.BookNotFoundError("Book not found with ISBN: " + isbn);
        }
    }

    private List<Author> addOrRetrieveAuthors(List<Author> authors) {
        List<Author> updatedAuthors = new ArrayList<>();
        for (Author author : authors) {
            Optional<Author> existingAuthor = authorRepository.findByName(author.getName());
            if (existingAuthor.isPresent()) {
                updatedAuthors.add(existingAuthor.get());
            } else {
                updatedAuthors.add(authorRepository.save(author));
            }
        }
        return updatedAuthors;
    }
}