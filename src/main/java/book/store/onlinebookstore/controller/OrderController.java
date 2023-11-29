package book.store.onlinebookstore.controller;

import book.store.onlinebookstore.dto.order.CreateOrderRequestDto;
import book.store.onlinebookstore.dto.order.OrderDto;
import book.store.onlinebookstore.dto.order.UpdateOrderRequestDto;
import book.store.onlinebookstore.dto.order.item.OrderItemDto;
import book.store.onlinebookstore.model.User;
import book.store.onlinebookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Tag(name = "Order management", description = "Endpoints for managing users orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Place an order based on your shopping cart",
            description = """
                                Place an order based on your shopping cart, 
                                then shopping cart is deleted
                    """)
    public OrderDto placeOrder(@RequestBody @Valid CreateOrderRequestDto requestDto,
                               Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.placeOrder(user.getId(), requestDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all orders",
            description = "Get all orders for user, default: page = 0, size = 10")
    public List<OrderDto> getAll(Authentication authentication,
                                 @PageableDefault(page = 0, size = 10) Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return orderService.getOrders(user.getId(), pageable);
    }

    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get info by order",
            description = "Get all order items by order id")
    public List<OrderItemDto> getOrderItemsByOrderId(Authentication authentication,
                                                     @PathVariable @Positive Long orderId) {
        User user = (User) authentication.getPrincipal();
        return orderService.getOrderItemsByOrderId(user.getId(), orderId);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get info about order item",
            description = "Get info about order item by order id and item id")
    public OrderItemDto getOrderItemsByOrderId(Authentication authentication,
                                               @PathVariable @Positive Long orderId,
                                               @PathVariable @Positive Long itemId) {
        User user = (User) authentication.getPrincipal();
        return orderService.getItemById(user.getId(), orderId, itemId);
    }

    @PatchMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status",
            description = "Update order status for order by id")
    public OrderDto updateOrderStatusById(@PathVariable @Positive Long orderId,
                                          @RequestBody @Valid UpdateOrderRequestDto requestDto) {
        return orderService.updateOrderStatus(orderId, requestDto);
    }
}
