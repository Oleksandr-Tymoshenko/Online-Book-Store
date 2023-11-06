package book.store.onlinebookstore.service;

import book.store.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import book.store.onlinebookstore.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto addCartItem(CreateCartItemRequestDto cartItem, Long userId);

    ShoppingCartDto getShoppingCart(Long userId);

    ShoppingCartDto updateCartItem(Long userId, Long itemId, Integer quantity);

    ShoppingCartDto deleteCartItemById(Long userId, Long itemId);
}
