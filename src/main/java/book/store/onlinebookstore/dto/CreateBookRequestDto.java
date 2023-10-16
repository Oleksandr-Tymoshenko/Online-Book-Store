package book.store.onlinebookstore.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record CreateBookRequestDto(
        @NotNull String title,
        @NotNull String author,
        @NotNull @Pattern(regexp = "^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$") String isbn,
        @NotNull @Min(0) BigDecimal price,
        String description, String coverImage) {

}
