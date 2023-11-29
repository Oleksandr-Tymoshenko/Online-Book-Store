package book.store.onlinebookstore.dto.order;

import book.store.onlinebookstore.dto.order.item.OrderItemDto;
import book.store.onlinebookstore.model.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record OrderDto(
        Long id,
        Long userId,
        Set<OrderItemDto> orderItems,
        LocalDateTime orderDate,
        String shippingAddress,
        BigDecimal total,
        Order.OrderStatus status
) {
}
