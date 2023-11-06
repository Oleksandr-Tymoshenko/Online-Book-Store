package book.store.onlinebookstore.dto.cartitem;

import jakarta.validation.constraints.Min;

public record UpdateCartItemRequestDto(@Min(1) Integer quantity) {
}
