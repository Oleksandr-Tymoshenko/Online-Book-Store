package book.store.onlinebookstore.dto.order;

import book.store.onlinebookstore.model.Order;
import jakarta.validation.constraints.NotBlank;

public record UpdateOrderRequestDto(
        @NotBlank
        Order.OrderStatus status) {
}
