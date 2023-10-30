package book.store.onlinebookstore.dto.category;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CreateCategoryRequestDto(
        @NotBlank @Length(max = 100)
        String name,
        @Length(max = 255)
        String description
) {
}
