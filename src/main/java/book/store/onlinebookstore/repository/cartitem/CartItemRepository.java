package book.store.onlinebookstore.repository.cartitem;

import book.store.onlinebookstore.model.CartItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByIdAndShoppingCartId(Long id, Long shoppingCartId);

    @EntityGraph(attributePaths = {"shoppingCart", "book"})
    Optional<CartItem> findCartItemByShoppingCartIdAndBookId(Long shoppingCartId, Long bookId);

}
