package book.store.onlinebookstore.service.impl;

import book.store.onlinebookstore.dto.order.CreateOrderRequestDto;
import book.store.onlinebookstore.dto.order.OrderDto;
import book.store.onlinebookstore.dto.order.UpdateOrderRequestDto;
import book.store.onlinebookstore.dto.order.item.OrderItemDto;
import book.store.onlinebookstore.exception.EntityNotFoundException;
import book.store.onlinebookstore.mapper.OrderItemMapper;
import book.store.onlinebookstore.mapper.OrderMapper;
import book.store.onlinebookstore.model.CartItem;
import book.store.onlinebookstore.model.Order;
import book.store.onlinebookstore.model.OrderItem;
import book.store.onlinebookstore.model.ShoppingCart;
import book.store.onlinebookstore.repository.order.OrderRepository;
import book.store.onlinebookstore.repository.order.item.OrderItemRepository;
import book.store.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import book.store.onlinebookstore.repository.user.UserRepository;
import book.store.onlinebookstore.service.OrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public OrderDto placeOrder(Long userId, CreateOrderRequestDto requestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Your shopping cart is empty"));
        Set<CartItem> cartItems = shoppingCart.getCartItems();

        Order newOrder = initNewOrder(requestDto, userId);
        orderRepository.save(newOrder);

        BigDecimal total = BigDecimal.ZERO;
        Set<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> {
                    OrderItem orderItem = orderItemMapper.fromCartItemToOrderItem(cartItem);
                    newOrder.setTotal(total.add(orderItem.getPrice()).add(newOrder.getTotal()));
                    orderItem.setOrder(newOrder);
                    return orderItemRepository.save(orderItem);
                }).collect(Collectors.toSet());
        newOrder.setOrderItems(orderItems);

        shoppingCartRepository.deleteById(userId);
        return orderMapper.toDto(newOrder);
    }

    @Override
    public List<OrderDto> getOrders(Long userId, Pageable pageable) {
        List<Order> allByUserId = orderRepository.findAllByUserId(userId, pageable);
        return allByUserId.stream().map(orderMapper::toDto).toList();
    }

    @Override
    @Transactional
    public List<OrderItemDto> getOrderItemsByOrderId(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find order by id "
                        + orderId));
        return order.getOrderItems().stream().map(orderItemMapper::toDto).toList();
    }

    @Override
    public OrderItemDto getItemById(Long userId, Long orderId, Long itemId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find order by id "
                        + orderId));
        OrderItem orderItem = orderItemRepository.findByIdAndOrderId(itemId, order.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find item by id " + itemId));
        return orderItemMapper.toDto(orderItem);
    }

    @Override
    @Transactional
    public OrderDto updateOrderStatus(Long orderId, UpdateOrderRequestDto requestDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find order by id "
                        + orderId));
        order.setStatus(requestDto.status());
        return orderMapper.toDto(order);
    }

    private Order initNewOrder(CreateOrderRequestDto requestDto, Long userId) {
        Order order = new Order();
        order.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find user by id" + userId)));
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setShippingAddress(requestDto.shippingAddress());
        order.setTotal(BigDecimal.ZERO);
        return order;
    }
}
