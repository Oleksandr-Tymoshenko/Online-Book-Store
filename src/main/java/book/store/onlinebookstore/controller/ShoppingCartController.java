package book.store.onlinebookstore.controller;

import book.store.onlinebookstore.dto.cartitem.CreateCartItemRequestDto;
import book.store.onlinebookstore.dto.cartitem.UpdateCartItemRequestDto;
import book.store.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import book.store.onlinebookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/cart")
@Tag(name = "Shopping cart management", description = "Endpoints for managing user shopping cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Add a new book to shopping cart",
            description = "Add a new book to shopping cart")
    public ShoppingCartDto addCartItem(@RequestBody @Valid CreateCartItemRequestDto requestDto,
                                       Authentication authentication) {
        return shoppingCartService.addCartItem(requestDto, authentication);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get shopping cart",
            description = "Get shopping cart")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        return shoppingCartService.getShoppingCart(authentication);
    }

    @PutMapping("/cart-items/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update quantity of cart item",
            description = "Endpoint for updating quantity of an item in shopping cart")
    public ShoppingCartDto updateCartItem(@PathVariable @Positive Long id,
                                          @RequestBody UpdateCartItemRequestDto requestDto,
                                          Authentication authentication) {
        return shoppingCartService.updateCartItem(authentication, id, requestDto.quantity());
    }

    @DeleteMapping("/cart-items/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Delete book from shopping cart",
            description = "Delete book from shopping cart by id")
    public ShoppingCartDto deleteCartItem(@PathVariable @Positive Long id,
                                          Authentication authentication) {
        return shoppingCartService.deleteCartItemById(authentication, id);
    }
}
