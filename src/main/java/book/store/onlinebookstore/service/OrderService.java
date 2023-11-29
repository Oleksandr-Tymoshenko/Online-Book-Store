package book.store.onlinebookstore.service;

import book.store.onlinebookstore.dto.order.CreateOrderRequestDto;
import book.store.onlinebookstore.dto.order.OrderDto;
import book.store.onlinebookstore.dto.order.UpdateOrderRequestDto;
import book.store.onlinebookstore.dto.order.item.OrderItemDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto placeOrder(Long userid, CreateOrderRequestDto requestDto);

    List<OrderDto> getOrders(Long userId, Pageable pageable);

    List<OrderItemDto> getOrderItemsByOrderId(Long userId, Long orderId);

    OrderItemDto getItemById(Long userId, Long orderId, Long itemId);

    OrderDto updateOrderStatus(Long orderId, UpdateOrderRequestDto requestDto);
}
