package book.store.onlinebookstore.service;

import book.store.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import book.store.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import book.store.onlinebookstore.model.User;

public interface ShoppingCartService {
    ShoppingCartDto addCartItem(CreateCartItemRequestDto cartItem, User user);

    ShoppingCartDto getShoppingCart(Long userId);

    ShoppingCartDto updateCartItem(Long userId, Long itemId, Integer quantity);

    ShoppingCartDto deleteCartItemById(Long userId, Long itemId);
}
