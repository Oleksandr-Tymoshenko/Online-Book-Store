package book.store.onlinebookstore.book;

import book.store.onlinebookstore.model.Book;
import book.store.onlinebookstore.repository.book.BookRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Check if findById returns correct book")
    @Sql(scripts = "classpath:database.scripts/book/add-one-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database.scripts/book/clear-book-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findById_ValidId_ReturnsBook() {
        Optional<Book> actual = bookRepository.findById(1L);
        Assertions.assertTrue(actual.isPresent());
    }

    @Test
    @DisplayName("Check if findAll returns list of books")
    @Sql(scripts = "classpath:database.scripts/book/add-three-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database.scripts/book/clear-book-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_ValidPageable_ReturnsListOfBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> books = bookRepository.findAll(pageable);
        Assertions.assertEquals(3, books.toList().size());
    }

    @Test
    @DisplayName("Check if findAllByCategoryId returns list of books")
    @Sql(scripts = "classpath:database.scripts/category/add-three-books-and-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database.scripts/category/delete-three-books-and-categories.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoriesId_ValidIdAndPageable_ReturnsListOfBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = bookRepository.findAllByCategoriesId(1L, pageable);
        Assertions.assertEquals(2, books.size());
    }
}
