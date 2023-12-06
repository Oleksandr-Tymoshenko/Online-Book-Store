package book.store.onlinebookstore.service;

import book.store.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import book.store.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {
    ShoppingCartDto addCartItem(CreateCartItemRequestDto cartItem, Authentication authentication);

    ShoppingCartDto getShoppingCart(Authentication authentication);

    ShoppingCartDto updateCartItem(Authentication authentication, Long itemId, Integer quantity);

    ShoppingCartDto deleteCartItemById(Authentication authentication, Long itemId);
}
