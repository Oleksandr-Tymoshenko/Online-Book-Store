package book.store.onlinebookstore.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import book.store.onlinebookstore.dto.cartitem.CartItemDto;
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
import book.store.onlinebookstore.service.impl.ShoppingCartServiceImpl;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("Check adding cart item shopping cart and if shopping cart dto is returned")
    void addCartItem_NotCreatedShoppingCartAndNewCartItem_ReturnsShoppingCartDto() {
        //given
        var requestDto = new CreateCartItemRequestDto(1L, 2);
        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");

        CartItem cartItem = new CartItem();
        cartItem.setQuantity(requestDto.quantity());
        CartItemDto cartItemDto = new CartItemDto(1L, "Test book", requestDto.quantity());
        ShoppingCart shoppingCart = new ShoppingCart();
        ShoppingCartDto expected = new ShoppingCartDto(
                user.getId(),
                user.getId(),
                Set.of(cartItemDto));

        Mockito.when(bookRepository.existsById(requestDto.bookId())).thenReturn(true);
        Mockito.when(cartItemRepository.findCartItemByShoppingCartIdAndBookId(user.getId(),
                requestDto.bookId())).thenReturn(Optional.empty());
        Mockito.when(cartItemMapper.toCartItem(requestDto)).thenReturn(cartItem);
        Mockito.when(bookRepository.getReferenceById(requestDto.bookId())).thenReturn(new Book());
        Mockito.when(shoppingCartRepository.findById(user.getId())).thenReturn(Optional.empty());
        Mockito.when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        Mockito.when(userRepository.findUserByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        Mockito.when(shoppingCartRepository.save(shoppingCart)).thenReturn(shoppingCart);
        Mockito.when(shoppingCartMapper.toShoppingCartDto(shoppingCart)).thenReturn(expected);
        Mockito.when(authentication.getName()).thenReturn(user.getEmail());

        //when
        ShoppingCartDto actual = shoppingCartService.addCartItem(requestDto, authentication);

        //then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(shoppingCartRepository, Mockito.times(1))
                .save(shoppingCart);
        Mockito.verify(cartItemRepository, Mockito.times(1))
                .save(cartItem);
    }

    @Test
    @DisplayName("Check adding existing cart item to shopping cart "
            + "and if shopping cart dto is returned")
    void addCartItem_ExistingCartItem_ReturnsShoppingCartDto() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        var requestDto = new CreateCartItemRequestDto(1L, 2);
        cartItem.setQuantity(requestDto.quantity());

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(user.getId());
        shoppingCart.setCartItems(Set.of(cartItem));
        cartItem.setShoppingCart(shoppingCart);
        CartItemDto cartItemDto = new CartItemDto(1L, "Test book", requestDto.quantity());

        ShoppingCartDto expected = new ShoppingCartDto(shoppingCart.getId(),
                shoppingCart.getId(),
                Set.of(cartItemDto));

        Mockito.when(bookRepository.existsById(requestDto.bookId())).thenReturn(true);
        Mockito.when(cartItemRepository
                        .findCartItemByShoppingCartIdAndBookId(user.getId(), requestDto.bookId()))
                .thenReturn(Optional.of(cartItem));
        Mockito.when(cartItemRepository.findByIdAndShoppingCartId(cartItem.getId(), user.getId()))
                .thenReturn(Optional.of(cartItem));
        Mockito.when(shoppingCartMapper.toShoppingCartDto(shoppingCart))
                .thenReturn(expected);
        Mockito.when(userRepository.findUserByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        Mockito.when(authentication.getName()).thenReturn(user.getEmail());

        //when
        ShoppingCartDto actual = shoppingCartService.addCartItem(requestDto, authentication);

        //then
        Assertions.assertEquals(expected, actual);
        Mockito.verify(cartItemRepository, Mockito.times(0))
                .save(cartItem);
    }

    @Test
    @DisplayName("Check if exception is thrown because of incorrect book id")
    void addCartItem_InvalidBookId_ThrowsException() {
        //given
        var requestDto = new CreateCartItemRequestDto(100L, 2);
        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");
        Mockito.when(bookRepository.existsById(requestDto.bookId())).thenReturn(false);

        //when
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> shoppingCartService.addCartItem(requestDto, authentication)
        );

        //then
        String expected = "Can't find book by id " + requestDto.bookId();
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Check if shopping cart dto is returned by id")
    void getShoppingCart_ValidId_ReturnsShoppingCartDto() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(user.getId());

        ShoppingCartDto expected = new ShoppingCartDto(shoppingCart.getId(),
                shoppingCart.getId(),
                Set.of());

        Mockito.when(shoppingCartRepository.findById(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        Mockito.when(shoppingCartMapper.toShoppingCartDto(shoppingCart)).thenReturn(expected);
        Mockito.when(userRepository.findUserByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        Mockito.when(authentication.getName()).thenReturn(user.getEmail());

        //when
        ShoppingCartDto actual = shoppingCartService.getShoppingCart(authentication);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Check if cart item is updated")
    void updateCartItem_ValidIds_ReturnsUpdatedShoppingCart() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQuantity(2);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(user.getId());
        shoppingCart.setUser(user);
        cartItem.setShoppingCart(shoppingCart);
        CartItemDto cartItemDto = new CartItemDto(1L, "Test book", 3);

        ShoppingCartDto expected = new ShoppingCartDto(
                user.getId(),
                user.getId(),
                Set.of(cartItemDto));

        Mockito.when(cartItemRepository.findByIdAndShoppingCartId(cartItem.getId(), user.getId()))
                .thenReturn(Optional.of(cartItem));
        Mockito.when(shoppingCartMapper.toShoppingCartDto(shoppingCart))
                .thenReturn(expected);
        Mockito.when(userRepository.findUserByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        Mockito.when(authentication.getName()).thenReturn(user.getEmail());

        //when
        ShoppingCartDto actual = shoppingCartService
                .updateCartItem(authentication, cartItem.getId(), 3);

        //then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Check if cart item is deleted")
    void deleteCartItemById_ValidId_ReturnsShoppingCartDto() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("email@gmail.com");
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(user.getId());
        shoppingCart.setUser(user);
        shoppingCart.setCartItems(new HashSet<>(Set.of(cartItem)));
        ShoppingCartDto expected = new ShoppingCartDto(user.getId(), user.getId(), Set.of());
        Mockito.when(shoppingCartRepository.findById(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        Mockito.when(cartItemRepository.findByIdAndShoppingCartId(cartItem.getId(), user.getId()))
                .thenReturn(Optional.of(cartItem));
        Mockito.when(shoppingCartMapper.toShoppingCartDto(shoppingCart))
                .thenReturn(expected);
        Mockito.when(userRepository.findUserByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        Mockito.when(authentication.getName()).thenReturn(user.getEmail());

        //when
        ShoppingCartDto actual = shoppingCartService.deleteCartItemById(authentication,
                cartItem.getId());

        //then
        Assertions.assertEquals(expected, actual);
    }
}
