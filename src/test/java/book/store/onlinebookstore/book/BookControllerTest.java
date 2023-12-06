package book.store.onlinebookstore.book;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.onlinebookstore.dto.book.BookDto;
import book.store.onlinebookstore.dto.book.CreateBookRequestDto;
import book.store.onlinebookstore.dto.book.UpdateBookRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Check creating book")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @Sql(scripts = "classpath:database.scripts/book/clear-book-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @SneakyThrows
    void createBook_ValidCreateRequestDto_ReturnsBookDto() {
        //given
        var requestDto = new CreateBookRequestDto(
                "Test book",
                "Test author",
                "123-123-0001",
                BigDecimal.valueOf(100),
                Set.of(1L, 2L),
                null,
                null
        );

        BookDto expected = new BookDto();
        expected.setId(1L);
        expected.setTitle(requestDto.title());
        expected.setAuthor(requestDto.author());
        expected.setIsbn(requestDto.isbn());
        expected.setPrice(requestDto.price());
        expected.setCategoriesIds(requestDto.categoriesIds());

        //when
        MvcResult result = mockMvc.perform(post("/api/books")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //then
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @DisplayName("Check if get all books returns list of books dto")
    @WithMockUser
    @Sql(scripts = "classpath:database.scripts/book/add-three-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database.scripts/book/clear-book-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @SneakyThrows
    void getAll_ValidPageable_ReturnsListOfBooks() {
        //given
        BookDto book1 = new BookDto();
        book1.setId(1L);
        book1.setTitle("test-book1");
        book1.setAuthor("test-author");
        book1.setIsbn("123-123-0001");
        book1.setPrice(BigDecimal.valueOf(100.99));
        book1.setCategoriesIds(Set.of());
        BookDto book2 = new BookDto();
        book2.setId(2L);
        book2.setTitle("test-book2");
        book2.setAuthor("test-author");
        book2.setIsbn("123-123-0002");
        book2.setPrice(BigDecimal.valueOf(200.99));
        book2.setCategoriesIds(Set.of());
        BookDto book3 = new BookDto();
        book3.setId(3L);
        book3.setTitle("test-book3");
        book3.setAuthor("test-author");
        book3.setIsbn("123-123-0003");
        book3.setPrice(BigDecimal.valueOf(300.99));
        book3.setCategoriesIds(Set.of());
        List<BookDto> expected = List.of(book1, book2, book3);

        //when
        MvcResult result = mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        BookDto[] actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto[].class);
        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @DisplayName("Check if get by id returns book dto")
    @WithMockUser
    @Sql(scripts = "classpath:database.scripts/book/add-one-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database.scripts/book/clear-book-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @SneakyThrows
    void getById_ValidId_ReturnsBookDto() {
        //given
        BookDto expected = new BookDto();
        expected.setId(1L);
        expected.setTitle("test-book");
        expected.setAuthor("test-author");
        expected.setIsbn("123-123-0001");
        expected.setPrice(BigDecimal.valueOf(100.99));
        expected.setCategoriesIds(Set.of());

        //when
        MvcResult result = mockMvc.perform(get("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Check if book is updated")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @Sql(scripts = "classpath:database.scripts/book/add-one-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database.scripts/book/clear-book-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @SneakyThrows
    void updateById_ValidId_ReturnsUpdatedBookDto() {
        //given
        var requestDto = new UpdateBookRequestDto(
                "Updated book",
                "Updated author",
                "123-123-0002",
                BigDecimal.valueOf(300),
                Set.of(),
                null,
                null
        );

        BookDto expected = new BookDto();
        expected.setId(1L);
        expected.setTitle(requestDto.title());
        expected.setAuthor(requestDto.author());
        expected.setIsbn(requestDto.isbn());
        expected.setPrice(requestDto.price());
        expected.setCategoriesIds(requestDto.categoriesIds());

        //when
        MvcResult result = mockMvc.perform(put("/api/books/1")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Check if book is deleted")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @Sql(scripts = "classpath:database.scripts/book/add-one-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database.scripts/book/clear-book-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @SneakyThrows
    void deleteById_ValidId_BookIsDeleted() {
        //when
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @DisplayName("Check if get all books returns list of books dto")
    @WithMockUser
    @Sql(scripts = "classpath:database.scripts/book/add-three-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database.scripts/book/clear-book-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @SneakyThrows
    void search_ValidValues_ReturnsListOfBooks() {
        //given
        BookDto book1 = new BookDto();
        book1.setId(1L);
        book1.setTitle("test-book1");
        book1.setAuthor("test-author");
        book1.setIsbn("123-123-0001");
        book1.setPrice(BigDecimal.valueOf(100.99));
        book1.setCategoriesIds(Set.of());
        BookDto book2 = new BookDto();
        book2.setId(2L);
        book2.setTitle("test-book2");
        book2.setAuthor("test-author");
        book2.setIsbn("123-123-0002");
        book2.setPrice(BigDecimal.valueOf(200.99));
        book2.setCategoriesIds(Set.of());
        List<BookDto> expected = List.of(book1, book2);

        //when
        MvcResult result = mockMvc.perform(
                        get("/api/books/search?titles=test-book1,test-book2&authors=test-author")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        BookDto[] actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDto[].class);
        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }
}
