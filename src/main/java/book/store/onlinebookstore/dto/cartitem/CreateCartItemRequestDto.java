package book.store.onlinebookstore.dto.cartitem;

import jakarta.validation.constraints.Positive;

public record CreateCartItemRequestDto(
        @Positive
        Long bookId,

        @Positive
        Integer quantity
) {
}
