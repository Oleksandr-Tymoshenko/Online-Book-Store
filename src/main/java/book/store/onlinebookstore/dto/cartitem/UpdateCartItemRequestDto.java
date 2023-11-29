package book.store.onlinebookstore.dto.cartitem;

import jakarta.validation.constraints.Positive;

public record UpdateCartItemRequestDto(@Positive Integer quantity) {
}
