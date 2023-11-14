package book.store.onlinebookstore.mapper;

import book.store.onlinebookstore.dto.order.item.OrderItemDto;
import book.store.onlinebookstore.model.CartItem;
import book.store.onlinebookstore.model.OrderItem;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = BookMapper.class
)
public interface OrderItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    OrderItemDto toDto(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "book", target = "book")
    @Mapping(source = "book.price", target = "price")
    OrderItem toOrderItem(CartItem cartItem);
}
