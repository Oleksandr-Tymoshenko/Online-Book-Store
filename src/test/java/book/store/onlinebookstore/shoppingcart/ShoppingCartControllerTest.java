package book.store.onlinebookstore.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import java.util.Set;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
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
@Sql(scripts = {"classpath:database.scripts/book/add-three-books.sql",
        "classpath:database.scripts/user/add-user.sql",
        "classpath:database.scripts/shoppingcart/add-shopping-cart.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"classpath:database.scripts/book/clear-book-table.sql",
        "classpath:database.scripts/shoppingcart/delete-shopping-cart.sql",
        "classpath:database.scripts/user/delete-user.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class ShoppingCartControllerTest {
    private static MockMvc mockMvc;
    private static final String DEFAULT_USERNAME = "email@gmail.com";
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
        assertNotNull(actual);
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
        assertNotNull(actual);
        assertEquals(expected, actual);
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
        assertNotNull(actual);
        assertEquals(expected, actual);
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
        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}
