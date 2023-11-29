package book.store.onlinebookstore.repository.order.item;

import book.store.onlinebookstore.model.OrderItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @EntityGraph(attributePaths = {"book"})
    Optional<OrderItem> findByIdAndOrderId(Long itemId, Long orderId);
}
