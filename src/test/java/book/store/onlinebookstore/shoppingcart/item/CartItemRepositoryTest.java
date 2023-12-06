package book.store.onlinebookstore.shoppingcart.item;

import book.store.onlinebookstore.model.CartItem;
import book.store.onlinebookstore.repository.cartitem.CartItemRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CartItemRepositoryTest {
    @Autowired
    private CartItemRepository cartItemRepository;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource) {
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
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database.scripts/shoppingcart/item/add-three-cart-items.sql")
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
                    new ClassPathResource(
                            "database.scripts/shoppingcart/item/delete-cart-items.sql")
            );
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
    @DisplayName("Check if cart item is returned by id")
    void findByIdAndShoppingCartId_ValidId_ReturnsCartItem() {
        //given
        CartItem expected = new CartItem();
        expected.setId(1L);
        expected.setQuantity(5);
        //when
        Optional<CartItem> actual = cartItemRepository.findByIdAndShoppingCartId(1L, 1L);
        //then
        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(expected, actual.get());
    }

    @Test
    @DisplayName("Check if cart item returns by shopping cart id and book id")
    void findCartItemByShoppingCartIdAndBookId_ValidId_ReturnsCartItem() {
        //given
        CartItem expected = new CartItem();
        expected.setId(2L);
        expected.setQuantity(10);
        //when
        Optional<CartItem> actual = cartItemRepository
                .findCartItemByShoppingCartIdAndBookId(1L, 2L);
        //then
        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(expected, actual.get());
    }

    @Test
    @DisplayName("Check if list of cart items is returned by shopping cart id")
    void findAllByShoppingCartId_ValidId_ReturnsListOfCartItems() {
        //given
        CartItem cartItem1 = new CartItem();
        cartItem1.setId(1L);
        cartItem1.setQuantity(5);
        CartItem cartItem2 = new CartItem();
        cartItem2.setId(2L);
        cartItem2.setQuantity(10);
        CartItem cartItem3 = new CartItem();
        cartItem3.setId(3L);
        cartItem3.setQuantity(15);
        List<CartItem> expected = List.of(cartItem1, cartItem2, cartItem3);
        //when
        List<CartItem> actual = cartItemRepository.findAllByShoppingCartId(1L);
        //then
        Assertions.assertEquals(expected, actual);
    }
}
