package book.store.onlinebookstore.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record BookDto(Long id, String title, String author, String isbn,
                      BigDecimal price, String description, String coverImage) {

}
