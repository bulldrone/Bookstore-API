package dxc.assessment.bookstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import dxc.assessment.bookstore.model.Book;
import dxc.assessment.bookstore.repository.AuthorRepository;
import dxc.assessment.bookstore.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private AuthorRepository authorRepository;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private ObjectMapper objectMapper;

	private Book book1Create;
	private Book book1Update;
	private Book book2Create;
	private Book book2Update;

	@BeforeEach
	void setUp() throws IOException {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		book1Create = objectMapper.readValue(Files.readString(Paths.get("src/test/data/Book1_create.json")), Book.class);
		book1Update = objectMapper.readValue(Files.readString(Paths.get("src/test/data/Book1_update.json")), Book.class);
		book2Create = objectMapper.readValue(Files.readString(Paths.get("src/test/data/Book2_create.json")), Book.class);
		book2Update = objectMapper.readValue(Files.readString(Paths.get("src/test/data/Book2_update.json")), Book.class);
		bookRepository.deleteAll();
		authorRepository.deleteAll();
	}

	@Test //add Book as User Role
	@WithMockUser(username = "user", roles = {"USER"})
	void addBookAsUserShouldSucceed() throws Exception {
		// Save authors individually if needed
		book1Create.getAuthors().forEach(authorRepository::save);

		// Send request to add a book
		mockMvc.perform(post("/api/v1/books")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(book1Create)))
				.andExpect(status().isOk());
	}

	@Test //add Book as Admin Role
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	void addBookAsAdminShouldSucceed() throws Exception {
		// Save authors individually if needed
		book2Create.getAuthors().forEach(authorRepository::save);

		// Send request to add a book as an admin
		mockMvc.perform(post("/api/v1/books")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(book2Create)))
				.andExpect(status().isOk());
	}

	@Test //update Book as User Role
	@WithMockUser(username = "user", roles = {"USER"})
	public void updateBookAsUserShouldSucceed() throws Exception {
		// Save authors and initial book
		book1Create.getAuthors().forEach(authorRepository::save);
		bookRepository.save(book1Create);

		// Convert the updated book data to JSON payload
		String jsonPayload = objectMapper.writeValueAsString(book1Update);

		// Perform update request
		mockMvc.perform(put("/api/v1/books/{isbn}", book1Update.getIsbn())
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonPayload))
				.andExpect(status().isOk());
	}

	@Test //Update Book as Admin Role
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	void updateBookAsAdminShouldSucceed() throws Exception {
		// Save authors and initial book
		book2Create.getAuthors().forEach(authorRepository::save);
		bookRepository.save(book2Create);

		// Convert the updated book data to JSON payload
		String jsonPayload = objectMapper.writeValueAsString(book2Update);

		// Perform update request as an admin
		mockMvc.perform(put("/api/v1/books/{isbn}", book2Update.getIsbn())
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonPayload))
				.andExpect(status().isOk());
	}

	@Test //Search Book by Title as User Role
	@WithMockUser(username = "user", roles = {"USER"})
	void SearchBookByTitleAsUserShouldSucceed() throws Exception {
		// Setup: save the initial book
		book1Create.getAuthors().forEach(authorRepository::save);
		bookRepository.save(book1Create);

		// Attempt to search by Title as user
		mockMvc.perform(get("/api/v1/books/search?title={title}", book1Create.getTitle()))
					.andExpect(status().isOk());
	}

	@Test // Search Book by Title as Admin Role
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	void searchBookByTitleAsAdminShouldSucceed() throws Exception {
		// Setup: save the initial book (book2)
		book2Create.getAuthors().forEach(authorRepository::save);
		bookRepository.save(book2Create);

		// Attempt to search by Title as admin
		mockMvc.perform(get("/api/v1/books/search?title={title}",
						book2Create.getTitle()))
				.andExpect(status().isOk());
	}

	@Test //Search Book by Author as User Role
	@WithMockUser(username = "user", roles = {"USER"})
	void SearchBookByAuthorAsUserShouldSucceed() throws Exception {
		// Setup: save the initial book
		book1Create.getAuthors().forEach(authorRepository::save);
		bookRepository.save(book1Create);

		// Attempt to search by title as user
		mockMvc.perform(get("/api/v1/books/search?author={author}",
						book1Create.getAuthors().stream().findFirst().get().getName()))
				.andExpect(status().isOk());
	}

	@Test // Search Book by Author as Admin Role
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	void searchBookByAuthorAsAdminShouldSucceed() throws Exception {
		// Setup: save the initial book (book2)
		book2Create.getAuthors().forEach(authorRepository::save);
		bookRepository.save(book2Create);

		// Attempt to search by Author as admin
		mockMvc.perform(get("/api/v1/books/search?author={author}",
						book2Create.getAuthors().stream().findFirst().get()
								.getName()))
				.andExpect(status().isOk());
	}

	@Test //Search Book by Title and Author as User Role
	@WithMockUser(username = "user", roles = {"USER"})
	void SearchBookByTitleByAuthorAsUserShouldSucceed() throws Exception {
		// Setup: save the initial book
		book1Create.getAuthors().forEach(authorRepository::save);
		bookRepository.save(book1Create);

		// Attempt to search by title as user
		mockMvc.perform(get("/api/v1/books/search?title={title}&author={author}",
						book1Create.getTitle(),
						book1Create.getAuthors().stream().findFirst().get().getName()))
				.andExpect(status().isOk());
	}

	@Test // Search Book by Title and Author as Admin Role
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	void searchBookByTitleAndAuthorAsAdminShouldSucceed() throws Exception {
		// Setup: save the initial book (book2)
		book2Create.getAuthors().forEach(authorRepository::save);
		bookRepository.save(book2Create);

		// Attempt to search by Title and Author as admin
		mockMvc.perform(get("/api/v1/books/search?title={title}&author={author}",
						book2Create.getTitle(),
						book2Create.getAuthors().stream().findFirst()
								.get().getName()))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", roles = {"USER"})
	void deleteBookAsUserShouldFail() throws Exception {
		// Setup: save the initial book
		book1Create.getAuthors().forEach(authorRepository::save);
		bookRepository.save(book1Create);

		mockMvc.perform(delete("/api/v1/books/{isbn}", book1Create.getIsbn()))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	void deleteBookAsAdminShouldSucceed() throws Exception {
		book2Create.getAuthors().forEach(authorRepository::save);
		bookRepository.save(book2Create);

		mockMvc.perform(delete("/api/v1/books/{isbn}", book2Create.getIsbn()))
				.andExpect(status().isNoContent());
	}
}
