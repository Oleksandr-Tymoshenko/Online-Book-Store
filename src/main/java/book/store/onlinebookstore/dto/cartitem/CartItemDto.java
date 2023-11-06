package book.store.onlinebookstore.dto.cartitem;

import lombok.Data;

@Data
public class CartItemDto {
    private Long id;
    private String bookTitle;
    private Integer quantity;
}
