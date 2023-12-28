package book.store.onlinebookstore.shoppingcart;

import book.store.onlinebookstore.model.ShoppingCart;
import book.store.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Check if findById returns correct shopping cart")
    @Sql(scripts = {
            "classpath:database.scripts/user/add-user.sql",
            "classpath:database.scripts/shoppingcart/add-shopping-cart.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            "classpath:database.scripts/shoppingcart/delete-shopping-cart.sql",
            "classpath:database.scripts/user/delete-user.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findById_ValidId_ReturnsShoppingCart() {
        //given
        ShoppingCart expected = new ShoppingCart();
        expected.setId(1L);
        //when
        Optional<ShoppingCart> actual = shoppingCartRepository.findById(1L);
        //then
        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(expected, actual.get());
    }
}
