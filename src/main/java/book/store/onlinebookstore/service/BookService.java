package book.store.onlinebookstore.service;

import book.store.onlinebookstore.dto.book.BookDto;
import book.store.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import book.store.onlinebookstore.dto.book.BookSearchParameters;
import book.store.onlinebookstore.dto.book.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto book);

    BookDto findById(Long id);

    List<BookDtoWithoutCategoryIds> findBooksByCategoryId(Long id, Pageable pageable);

    List<BookDto> search(BookSearchParameters searchParameters, Pageable pageable);

    List<BookDto> findAll(Pageable pageable);

    void deleteById(Long id);

    BookDto updateById(Long id, CreateBookRequestDto requestBookDto);
}
