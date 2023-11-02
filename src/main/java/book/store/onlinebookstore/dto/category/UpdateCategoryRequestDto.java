package book.store.onlinebookstore.dto.category;

import org.hibernate.validator.constraints.Length;

public record UpdateCategoryRequestDto(
        @Length(max = 100)
        String name,
        @Length(max = 255)
        String description
) {
}
