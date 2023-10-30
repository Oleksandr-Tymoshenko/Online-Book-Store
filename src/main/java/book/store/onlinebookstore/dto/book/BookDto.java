package book.store.onlinebookstore.dto.book;

import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;

@Data
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private Set<Long> categoriesIds;
    private String description;
    private String coverImage;
}
