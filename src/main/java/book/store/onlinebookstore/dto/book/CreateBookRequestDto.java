package book.store.onlinebookstore.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Set;

public record CreateBookRequestDto(
        @NotBlank String title,
        @NotBlank String author,
        @NotBlank @Pattern(regexp = "^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$") String isbn,
        @NotNull @Positive BigDecimal price,
        @NotEmpty Set<@Positive Long> categoriesIds,
        String description,
        String coverImage) {

}
