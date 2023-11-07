package book.store.onlinebookstore.dto.shoppingcart;

import book.store.onlinebookstore.dto.cartitem.CartItemDto;
import java.util.Set;
import lombok.Data;

@Data
public class ShoppingCartDto {
    private Long id;
    private Long userId;
    private Set<CartItemDto> cartItems;
}
