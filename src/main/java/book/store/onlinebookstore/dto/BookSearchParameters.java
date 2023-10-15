package book.store.onlinebookstore.dto;

import lombok.Data;

@Data
public class BookSearchParameters {
    private String[] titles;
    private String[] authors;
}
