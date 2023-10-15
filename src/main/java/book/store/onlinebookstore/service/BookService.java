package book.store.onlinebookstore.service;

import book.store.onlinebookstore.dto.BookDto;
import book.store.onlinebookstore.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto book);

    BookDto findById(Long id);

    List<BookDto> findAll();

    void deleteById(Long id);

    BookDto updateById(Long id, CreateBookRequestDto requestBookDto);
}
