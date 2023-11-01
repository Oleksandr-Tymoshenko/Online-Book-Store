package book.store.onlinebookstore.dto.book;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Set;

public record UpdateBookRequestDto(
        String title,
        String author,
        @Pattern(regexp = "^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$") String isbn,
        @Positive BigDecimal price,
        Set<Long> categoriesIds,
        String description, String coverImage) {

}
