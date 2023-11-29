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
    public ShoppingCartDto addCartItem(CreateCartItemRequestDto requestDto, User user) {
        if (!bookRepository.existsById(requestDto.bookId())) {
            throw new EntityNotFoundException("Can't find book by id " + requestDto.bookId());
        }
        Optional<CartItem> cartItemByBookId = cartItemRepository
                .findCartItemByShoppingCartIdAndBookId(user.getId(), requestDto.bookId());
        if (cartItemByBookId.isPresent()) {
            CartItem cartItem = cartItemByBookId.get();
            return updateCartItem(user.getId(), cartItem.getId(), requestDto.quantity());
        }

        CartItem cartItem = cartItemMapper.toCartItem(requestDto);
        Book bookFromDb = bookRepository.getReferenceById(requestDto.bookId());
        cartItem.setBook(bookFromDb);
        ShoppingCart shoppingCart = getOrCreateShoppingCart(user);
        shoppingCart.addCartItem(cartItem);

        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto getShoppingCart(Long userId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("The shopping cart doesn’t exist"));
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
                .orElseThrow(() -> new EntityNotFoundException("The shopping cart doesn’t exist"));
        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(itemId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find item by id "
                        + itemId + " in your shopping cart"));
        shoppingCart.removeCartItem(cartItem);
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    private ShoppingCart getOrCreateShoppingCart(User user) {
        return shoppingCartRepository.findById(user.getId())
                .orElseGet(() -> {
                    ShoppingCart newShoppingCart = new ShoppingCart();
                    newShoppingCart.setUser(userRepository.getReferenceById(user.getId()));
                    return shoppingCartRepository.save(newShoppingCart);
                });
    }
}
