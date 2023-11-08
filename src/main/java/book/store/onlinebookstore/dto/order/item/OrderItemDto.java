package book.store.onlinebookstore.dto.order.item;

import java.math.BigDecimal;

public record OrderItemDto(
        Long id,
        Long bookId,
        String bookTitle,
        Integer quantity,
        BigDecimal price
) {
}
