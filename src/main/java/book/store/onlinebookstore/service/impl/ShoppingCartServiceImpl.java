package book.store.onlinebookstore.service.impl;

import book.store.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import book.store.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import book.store.onlinebookstore.exception.EntityNotFoundException;
import book.store.onlinebookstore.mapper.CartItemMapper;
import book.store.onlinebookstore.mapper.ShoppingCartMapper;
import book.store.onlinebookstore.model.Book;
import book.store.onlinebookstore.model.CartItem;
import book.store.onlinebookstore.model.ShoppingCart;
import book.store.onlinebookstore.model.User;
import book.store.onlinebookstore.repository.book.BookRepository;
import book.store.onlinebookstore.repository.cartitem.CartItemRepository;
import book.store.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import book.store.onlinebookstore.repository.user.UserRepository;
import book.store.onlinebookstore.service.ShoppingCartService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ShoppingCartDto addCartItem(CreateCartItemRequestDto requestDto, Long userId) {
        Optional<CartItem> cartItemByBookId = cartItemRepository
                .findCartItemByShoppingCartIdAndBookId(userId, requestDto.bookId());
        if (cartItemByBookId.isPresent()) {
            CartItem cartItem = cartItemByBookId.get();
            return updateCartItem(userId, cartItem.getId(), requestDto.quantity());
        }
        CartItem cartItem = cartItemMapper.toCartItem(requestDto);
        Book bookFromDb = bookRepository.findById(requestDto.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id "
                        + requestDto.bookId()));
        cartItem.setBook(bookFromDb);
        ShoppingCart shoppingCart = shoppingCartRepository.findById(userId)
                .orElseGet(() -> {
                    ShoppingCart newShoppingCart = new ShoppingCart();
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new EntityNotFoundException("Can't find user by id "
                                    + userId));
                    newShoppingCart.setUser(user);
                    return shoppingCartRepository.save(newShoppingCart);
                });
        shoppingCart.addCartItem(cartItem);
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto getShoppingCart(Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Your shopping cart is empty"));
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto updateCartItem(Long userId, Long itemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(itemId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find item by id "
                        + itemId + " in your shopping cart"));
        cartItem.setQuantity(quantity);
        return shoppingCartMapper.toShoppingCartDto(cartItem.getShoppingCart());
    }

    @Override
    @Transactional
    public ShoppingCartDto deleteCartItemById(Long userId, Long itemId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Your shopping cart is empty"));
        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(itemId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find item by id "
                        + itemId + " in your shopping cart"));
        cartItemRepository.delete(cartItem);
        shoppingCart.removeCartItem(cartItem);
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }
}
