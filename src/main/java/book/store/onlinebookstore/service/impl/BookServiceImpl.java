package book.store.onlinebookstore.service.impl;

import book.store.onlinebookstore.dto.book.BookDto;
import book.store.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import book.store.onlinebookstore.dto.book.BookSearchParameters;
import book.store.onlinebookstore.dto.book.CreateBookRequestDto;
import book.store.onlinebookstore.dto.book.UpdateBookRequestDto;
import book.store.onlinebookstore.exception.EntityNotFoundException;
import book.store.onlinebookstore.mapper.BookMapper;
import book.store.onlinebookstore.model.Book;
import book.store.onlinebookstore.repository.book.BookRepository;
import book.store.onlinebookstore.repository.book.BookSpecificationBuilder;
import book.store.onlinebookstore.service.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder specificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto book) {
        return bookMapper.toDto(bookRepository.save(bookMapper.toBook(book)));
    }

    @Override
    public BookDto getById(Long id) {
        return bookRepository
                .findById(id)
                .map(bookMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Book was not found by id " + id));
    }

    @Override
    public List<BookDtoWithoutCategoryIds> findBooksByCategoryId(Long id, Pageable pageable) {
        return bookRepository.findAllByCategoriesId(id, pageable).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }

    @Override
    public List<BookDto> search(BookSearchParameters searchParameters, Pageable pageable) {
        return bookRepository.findAll(specificationBuilder.build(searchParameters), pageable)
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto updateById(Long id, UpdateBookRequestDto requestBookDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id " + id));
        bookMapper.updateBook(requestBookDto, book);
        return bookMapper.toDto(bookRepository.save(book));
    }
}
