package book.store.onlinebookstore.dto.shoppingcart;

import book.store.onlinebookstore.dto.cartitem.CartItemDto;
import java.util.Set;

public record ShoppingCartDto(Long id, Long userId, Set<CartItemDto> cartItems) {
}
