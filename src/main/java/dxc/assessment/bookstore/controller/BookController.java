package dxc.assessment.bookstore.controller;

import dxc.assessment.bookstore.error.CustomExceptions;
import dxc.assessment.bookstore.model.Book;
import dxc.assessment.bookstore.repository.BookRepository;
import dxc.assessment.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    // Add
    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        if(book.getAuthors().isEmpty()){
            throw new CustomExceptions.MissingJsonContentError("Add book fail! Book has no Author!");
        }
        return ResponseEntity.ok(bookService.addBook(book));
    }

    // Update
    @PutMapping("/{isbn}")
    public ResponseEntity<Book> updateBook(@PathVariable String isbn, @RequestBody Book book) {
        if(book.getAuthors().isEmpty()){
            throw new CustomExceptions.MissingJsonContentError("Update book fail! Book has no Author!");
        }
        return ResponseEntity.ok(bookService.updateBook(isbn, book));
    }

    @GetMapping("/")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getAllBooks(); // Ensure this method is implemented in BookService
        return ResponseEntity.ok(books);
    }

    //Get by Id
    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        Book book = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(book);
    }

    // Find books by title and author
    @GetMapping("/search")
    public ResponseEntity<List<Book>> getBooksByTitleAndAuthor(@RequestParam(required = false) String title,
                                                               @RequestParam(required = false) String author) {
        List<Book> books = new ArrayList<Book>();
        Optional<String> titleOptional = Optional.ofNullable(title);
        Optional<String> authorOptional = Optional.ofNullable(author);

        if(titleOptional.isPresent() && authorOptional.isPresent()) {
            books = bookService.findByTitleAndAuthorsContaining(title, author);
        } else if (titleOptional.isPresent()) {
            books = bookService.findByTitle(title);
        } else if (authorOptional.isPresent()) {
            books = bookService.findByAuthorsContaining(author);
        } else {
            throw new CustomExceptions.MissingParamQueryError("Search book fail! Neither Title or Author Param has been specified for search.");
        }

        return ResponseEntity.ok(books);
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        if(hasAdminRole){
            return bookService.deleteBook(isbn);
        } else {
            throw new CustomExceptions.UnauthorizedError("Delete book fail! Current user has no ADMIN role");
        }

    }
}