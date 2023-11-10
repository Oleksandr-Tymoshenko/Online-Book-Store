package book.store.onlinebookstore.dto.order;

import book.store.onlinebookstore.model.Order.OrderStatus;
import jakarta.validation.constraints.NotBlank;

public record UpdateOrderRequestDto(
        @NotBlank
        OrderStatus status) {
}
