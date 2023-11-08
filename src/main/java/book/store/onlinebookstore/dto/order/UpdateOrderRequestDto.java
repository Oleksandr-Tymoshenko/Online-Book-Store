package book.store.onlinebookstore.dto.order;

import book.store.onlinebookstore.model.Order;

public record UpdateOrderRequestDto(Order.OrderStatus status) {
}
