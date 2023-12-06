package book.store.onlinebookstore.shoppingcart;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.onlinebookstore.dto.cartitem.CartItemDto;
import book.store.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import book.store.onlinebookstore.dto.cartitem.UpdateCartItemRequestDto;
import book.store.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
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
public class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;
    private static final String DEFAULT_USERNAME = "email@gmail.com";
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext,
                          @Autowired DataSource dataSource) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database.scripts/book/add-three-books.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database.scripts/user/add-user.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database.scripts/shoppingcart/add-shopping-cart.sql")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database.scripts/book/clear-book-table.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database.scripts/shoppingcart/delete-shopping-cart.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database.scripts/user/delete-user.sql")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Check if new cart item is added")
    @WithMockUser(username = DEFAULT_USERNAME)
    @Sql(scripts = "classpath:database.scripts/shoppingcart/delete-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database.scripts/shoppingcart/item/delete-cart-items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @SneakyThrows
    void addCartItem_ValidRequestDto_ReturnsShoppingCart() {
        //given
        var requestDto = new CreateCartItemRequestDto(1L, 10);
        CartItemDto cartItemDto = new CartItemDto(1L, "test-book1", 10);
        var expected = new ShoppingCartDto(1L, 1L, Set.of(cartItemDto));

        //when
        MvcResult result = mockMvc.perform(post("/api/cart")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        //then
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    @DisplayName("Check if shopping cart is returned")
    @WithMockUser(username = DEFAULT_USERNAME)
    @Sql(scripts = {"classpath:database.scripts/shoppingcart/item/add-three-cart-items.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database.scripts/shoppingcart/item/delete-cart-items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @SneakyThrows
    void getShoppingCart_ValidUser_ReturnsShoppingCartDto() {
        //given
        CartItemDto cartItemDto1 = new CartItemDto(1L, "test-book1", 5);
        CartItemDto cartItemDto2 = new CartItemDto(2L, "test-book2", 10);
        CartItemDto cartItemDto3 = new CartItemDto(3L, "test-book3", 15);
        var expected = new ShoppingCartDto(1L, 1L,
                Set.of(cartItemDto1, cartItemDto2, cartItemDto3));

        //when
        MvcResult result = mockMvc.perform(get("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        Assertions.assertNotNull(actual);
        org.assertj.core.api.Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Check if cart item quantity is updated")
    @WithMockUser(username = DEFAULT_USERNAME)
    @Sql(scripts = {"classpath:database.scripts/shoppingcart/item/add-three-cart-items.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database.scripts/shoppingcart/item/delete-cart-items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @SneakyThrows
    void updateCartItem_ValidQuantity_ReturnsUpdatedShoppingCart() {
        //given
        var requestDto = new UpdateCartItemRequestDto(100);
        CartItemDto cartItemDto1 = new CartItemDto(1L, "test-book1", 100);
        CartItemDto cartItemDto2 = new CartItemDto(2L, "test-book2", 10);
        CartItemDto cartItemDto3 = new CartItemDto(3L, "test-book3", 15);
        var expected = new ShoppingCartDto(1L, 1L,
                Set.of(cartItemDto1, cartItemDto2, cartItemDto3));

        //when
        MvcResult result = mockMvc.perform(put("/api/cart/cart-items/1")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        Assertions.assertNotNull(actual);
        org.assertj.core.api.Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Check if cart item is deleted from shopping cart")
    @WithMockUser(username = DEFAULT_USERNAME)
    @Sql(scripts = {"classpath:database.scripts/shoppingcart/item/add-three-cart-items.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database.scripts/shoppingcart/item/delete-cart-items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @SneakyThrows
    void deleteCartItem_ValidId_ReturnsUpdatedShoppingCart() {
        //given
        CartItemDto cartItemDto1 = new CartItemDto(1L, "test-book1", 5);
        CartItemDto cartItemDto2 = new CartItemDto(2L, "test-book2", 10);
        var expected = new ShoppingCartDto(1L, 1L,
                Set.of(cartItemDto1, cartItemDto2));

        //when
        MvcResult result = mockMvc.perform(delete("/api/cart/cart-items/3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //then
        ShoppingCartDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                ShoppingCartDto.class);
        Assertions.assertNotNull(actual);
        org.assertj.core.api.Assertions.assertThat(actual).isEqualTo(expected);
    }
}
