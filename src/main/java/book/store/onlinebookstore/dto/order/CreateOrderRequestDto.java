package book.store.onlinebookstore.dto.order;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CreateOrderRequestDto(
        @NotBlank
        @Length(min = 2, max = 255)
        String shippingAddress) {
}
