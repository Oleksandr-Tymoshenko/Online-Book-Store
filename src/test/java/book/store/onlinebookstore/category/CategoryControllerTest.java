package book.store.onlinebookstore.category;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import book.store.onlinebookstore.dto.category.CategoryDto;
import book.store.onlinebookstore.dto.category.CreateCategoryRequestDto;
import book.store.onlinebookstore.dto.category.UpdateCategoryRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerTest {
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

    @AfterEach
    void tearDown(@Autowired DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database.scripts/category/delete-three-books-and-categories.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database.scripts/category/clear-categories-table.sql")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @DisplayName("Check creating category")
    @SneakyThrows
    void createCategory_ValidCreateRequestDto_ReturnsCategoryDto() {
        //given
        var requestDto = new CreateCategoryRequestDto("New category", null);
        CategoryDto expected = new CategoryDto(1L, requestDto.name(), null);

        //when
        MvcResult result = mockMvc.perform(post("/api/categories")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //then
        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @DisplayName("Check if get all categories returns list of categories dto")
    @WithMockUser
    @Sql(scripts = "classpath:database.scripts/category/add-three-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @SneakyThrows
    void getAll_ValidPageable_ReturnsListOfBooks() {
        //given
        CategoryDto categoryDto1 = new CategoryDto(1L, "Test category 1", null);
        CategoryDto categoryDto2 = new CategoryDto(2L, "Test category 2", null);
        CategoryDto categoryDto3 = new CategoryDto(3L, "Test category 3", null);
        List<CategoryDto> expected = List.of(categoryDto1, categoryDto2, categoryDto3);

        //when
        MvcResult result = mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        CategoryDto[] actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto[].class);
        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @DisplayName("Check if category dto is returned by id")
    @WithMockUser
    @Sql(scripts = "classpath:database.scripts/category/add-three-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @SneakyThrows
    void getCategoryById_ValidId_ReturnsCategoryDto() {
        //given
        CategoryDto expected = new CategoryDto(1L, "Test category 1", null);

        //when
        MvcResult result = mockMvc.perform(get("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Check if list of books without categories is returned by category id")
    @WithMockUser
    @Sql(scripts = "classpath:database.scripts/category/add-three-books-and-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @SneakyThrows
    void getBooksByCategoryId_ValidId_ReturnsListOfBookDtoWithoutCategories() {
        //given
        var bookDto1 = new BookDtoWithoutCategoryIds(1L,
                "test-book1",
                "test-author",
                "123-123-0001",
                BigDecimal.valueOf(100.99),
                null,
                null);
        var bookDto2 = new BookDtoWithoutCategoryIds(3L,
                "test-book3",
                "test-author",
                "123-123-0003",
                BigDecimal.valueOf(300.99),
                null,
                null);
        var expected = List.of(bookDto1, bookDto2);

        //when
        MvcResult result = mockMvc.perform(get("/api/categories/1/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        var actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookDtoWithoutCategoryIds[].class);
        Assertions.assertEquals(expected.size(), actual.length);
        Assertions.assertEquals(expected, Arrays.stream(actual).toList());
    }

    @Test
    @DisplayName("Check if category is updated by id")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @Sql(scripts = "classpath:database.scripts/category/add-three-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @SneakyThrows
    void updateCategory_ValidRequestDto_ReturnsCategoryDto() {
        //given
        var requestDto = new UpdateCategoryRequestDto("Updated category", null);
        CategoryDto expected = new CategoryDto(1L, requestDto.name(), null);

        //when
        MvcResult result = mockMvc.perform(put("/api/categories/1")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        CategoryDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                CategoryDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Check if category is deleted by id")
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    @Sql(scripts = "classpath:database.scripts/category/add-three-categories.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @SneakyThrows
    void deleteCategory_ValidId_DeletesCategory() {
        //when
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent())
                .andReturn();
        //then
        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
