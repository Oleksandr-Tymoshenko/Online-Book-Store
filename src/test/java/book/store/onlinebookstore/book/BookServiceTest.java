package book.store.onlinebookstore.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import book.store.onlinebookstore.dto.book.BookDto;
import book.store.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import book.store.onlinebookstore.dto.book.BookSearchParameters;
import book.store.onlinebookstore.dto.book.CreateBookRequestDto;
import book.store.onlinebookstore.dto.book.UpdateBookRequestDto;
import book.store.onlinebookstore.exception.EntityNotFoundException;
import book.store.onlinebookstore.mapper.BookMapper;
import book.store.onlinebookstore.model.Book;
import book.store.onlinebookstore.model.Category;
import book.store.onlinebookstore.repository.book.BookRepository;
import book.store.onlinebookstore.repository.book.BookSpecificationBuilder;
import book.store.onlinebookstore.repository.category.CategoryRepository;
import book.store.onlinebookstore.service.impl.BookServiceImpl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookSpecificationBuilder specificationBuilder;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Check if book is saved and correct dto is returned")
    void save_ValidCreateBookRequestDto_ReturnsBookDtoWithId() {
        //given
        CreateBookRequestDto requestDto = new CreateBookRequestDto(
                "testBook",
                "testAuthor",
                "123-123-0001",
                BigDecimal.valueOf(100),
                Set.of(1L, 2L),
                null,
                null
        );

        Book book = new Book();
        book.setTitle(requestDto.title());
        book.setAuthor(requestDto.author());
        book.setIsbn(requestDto.isbn());
        book.setPrice(requestDto.price());
        Set<Category> categories = requestDto.categoriesIds().stream()
                .map(id -> {
                    Category category = new Category();
                    category.setId(id);
                    return category;
                })
                .collect(Collectors.toSet());
        book.setCategories(categories);

        BookDto expected = new BookDto();
        expected.setId(1L);
        expected.setTitle(requestDto.title());
        expected.setAuthor(requestDto.author());
        expected.setIsbn(requestDto.isbn());
        expected.setPrice(requestDto.price());
        expected.setCategoriesIds(requestDto.categoriesIds());

        Mockito.when(bookMapper.toBook(requestDto)).thenReturn(book);
        Mockito.when(categoryRepository.findAllById(requestDto.categoriesIds()))
                .thenReturn(new ArrayList<>(categories));
        Mockito.when(bookRepository.save(book)).thenReturn(book);
        Mockito.when(bookMapper.toDto(book)).thenReturn(expected);

        //when
        BookDto actual = bookService.save(requestDto);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
    }

    @Test
    @DisplayName("Check if get book by id works")
    void getById_ValidId_ReturnsBookDto() {
        //given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("testBook");

        BookDto bookDto = new BookDto();
        bookDto.setId(bookId);
        bookDto.setTitle(book.getTitle());

        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        Mockito.when(bookMapper.toDto(book)).thenReturn(bookDto);

        //when
        BookDto bookByIdDto = bookService.getById(bookId);

        //then
        Assertions.assertThat(bookByIdDto).isEqualTo(bookDto);
        Mockito.verify(bookRepository, Mockito.times(1)).findById(bookId);
    }

    @Test
    @DisplayName("Check if get book by id throws exception with incorrect id")
    void getById_InvalidId_ThrowsEntityNotFound() {
        //given
        Long bookId = -1L;
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.getById(bookId)
        );

        //then
        String expected = "Book was not found by id " + bookId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Check if List of books is returned by category id")
    void findBooksByCategoryId_ValidId_ReturnListOfBookDto() {
        //given

        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book1");
        book1.setAuthor("Test Author1");
        book1.setIsbn("123-123-0001");
        book1.setPrice(BigDecimal.valueOf(100));
        var bookDto1 = new BookDtoWithoutCategoryIds(
                book1.getId(),
                book1.getTitle(),
                book1.getAuthor(),
                book1.getIsbn(),
                book1.getPrice(),
                book1.getDescription(),
                book1.getCoverImage()
        );

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book2");
        book2.setAuthor("Test Author2");
        book2.setIsbn("123-123-0002");
        book2.setPrice(BigDecimal.valueOf(200));
        List<Book> books = List.of(book1, book2);
        var bookDto2 = new BookDtoWithoutCategoryIds(
                book2.getId(),
                book2.getTitle(),
                book2.getAuthor(),
                book2.getIsbn(),
                book2.getPrice(),
                book2.getDescription(),
                book2.getCoverImage()
        );
        Long categoryId = 1L;
        List<BookDtoWithoutCategoryIds> expected = List.of(bookDto1, bookDto2);

        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(bookRepository.findAllByCategoriesId(categoryId, pageable))
                .thenReturn(books);
        Mockito.when(bookMapper.toDtoWithoutCategories(book1))
                .thenReturn(bookDto1);
        Mockito.when(bookMapper.toDtoWithoutCategories(book2))
                .thenReturn(bookDto2);

        //when
        List<BookDtoWithoutCategoryIds> actual = bookService
                .findBooksByCategoryId(categoryId, pageable);

        //then
        Assertions.assertThat(expected).isEqualTo(actual);
        Mockito.verify(bookRepository, Mockito.times(1))
                .findAllByCategoriesId(categoryId, pageable);
    }

    @Test
    @DisplayName("Check if search books is working")
    void search_ValidSearchParams_ReturnsListOfBooks() {
        //given
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book1");
        book1.setAuthor("Test author");
        book1.setIsbn("123-123-0001");
        book1.setPrice(BigDecimal.valueOf(100));

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book2");
        book2.setAuthor("Test author");
        book2.setIsbn("123-123-0002");
        book2.setPrice(BigDecimal.valueOf(200));

        BookDto bookDto1 = new BookDto();
        bookDto1.setId(book1.getId());
        bookDto1.setTitle(book1.getTitle());
        bookDto1.setAuthor(book1.getAuthor());
        bookDto1.setIsbn(book1.getIsbn());
        bookDto1.setPrice(book1.getPrice());

        BookDto bookDto2 = new BookDto();
        bookDto2.setId(book2.getId());
        bookDto2.setTitle(book2.getTitle());
        bookDto2.setAuthor(book2.getAuthor());
        bookDto2.setIsbn(book2.getIsbn());
        bookDto2.setPrice(book2.getPrice());
        List<BookDto> expected = List.of(bookDto1, bookDto2);

        var bookSearchParameters = new BookSearchParameters(
                new String[]{"Book1", "Book2"},
                new String[]{"Test author"});
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> books = new PageImpl<>(List.of(book1, book2));

        Mockito.when(bookRepository
                        .findAll(specificationBuilder.build(bookSearchParameters), pageable))
                .thenReturn(books);
        Mockito.when(bookMapper.toDto(book1))
                .thenReturn(bookDto1);
        Mockito.when(bookMapper.toDto(book2))
                .thenReturn(bookDto2);
        //when
        List<BookDto> actual = bookService.search(bookSearchParameters, pageable);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Check if findAll books works")
    void findAll_ValidPageable_ReturnsAllBooks() {
        //given
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book1");
        book1.setAuthor("Test Author1");
        book1.setIsbn("123-123-0001");
        book1.setPrice(BigDecimal.valueOf(100));

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book2");
        book2.setAuthor("Test Author2");
        book2.setIsbn("123-123-0002");
        book2.setPrice(BigDecimal.valueOf(200));

        BookDto bookDto1 = new BookDto();
        bookDto1.setId(book1.getId());
        bookDto1.setTitle(book1.getTitle());
        bookDto1.setAuthor(book1.getAuthor());
        bookDto1.setIsbn(book1.getIsbn());
        bookDto1.setPrice(book1.getPrice());

        BookDto bookDto2 = new BookDto();
        bookDto2.setId(book2.getId());
        bookDto2.setTitle(book2.getTitle());
        bookDto2.setAuthor(book2.getAuthor());
        bookDto2.setIsbn(book2.getIsbn());
        bookDto2.setPrice(book2.getPrice());
        List<BookDto> expected = List.of(bookDto1, bookDto2);
        Page<Book> books = new PageImpl<>(List.of(book1, book2));

        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(bookRepository.findAll(pageable))
                .thenReturn(books);
        Mockito.when(bookMapper.toDto(book1))
                .thenReturn(bookDto1);
        Mockito.when(bookMapper.toDto(book2))
                .thenReturn(bookDto2);

        //when
        List<BookDto> actual = bookService.findAll(pageable);

        //then
        Assertions.assertThat(expected).isEqualTo(actual);
        Mockito.verify(bookRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Check if delete book is invoked")
    void deleteById_ValidId_DeletesBook() {
        //given
        Long bookId = 1L;

        //when
        bookService.deleteById(bookId);

        //then
        Mockito.verify(bookRepository, Mockito.times(1)).deleteById(bookId);
    }

    @Test
    @DisplayName("Check if deleteById book throws exception with incorrect index")
    void deleteById_InvalidId_ThrowsException() {
        //given
        Long bookId = -1L;

        //when
        IndexOutOfBoundsException exception = assertThrows(
                IndexOutOfBoundsException.class,
                () -> bookService.deleteById(bookId)
        );

        //then
        String expected = "Index cannot be less then 1";
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Check if updateById book returns updated bookDto")
    void updateById_ValidId_ReturnsUpdatedBookDto() {
        //given
        Long bookId = 1L;
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setIsbn("123-123-0002");
        book.setPrice(BigDecimal.valueOf(200));
        Mockito.when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(book));

        var requestDto = new UpdateBookRequestDto(
                "UpdatedBook",
                "UpdatedAuthor",
                "123-123-0003",
                BigDecimal.valueOf(300),
                Set.of(1L, 2L),
                null,
                null
        );
        Set<Category> categories = requestDto.categoriesIds().stream()
                .map(id -> {
                    Category category = new Category();
                    category.setId(id);
                    return category;
                })
                .collect(Collectors.toSet());
        Mockito.when(categoryRepository.findAllById(requestDto.categoriesIds()))
                .thenReturn(new ArrayList<>(categories));

        BookDto expected = new BookDto();
        expected.setId(bookId);
        expected.setTitle(requestDto.title());
        expected.setAuthor(requestDto.author());
        expected.setIsbn(requestDto.isbn());
        expected.setPrice(requestDto.price());
        expected.setCategoriesIds(requestDto.categoriesIds());

        Mockito.when(bookRepository.save(book)).thenReturn(book);
        Mockito.when(bookMapper.toDto(book)).thenReturn(expected);

        //when
        BookDto actual = bookService.updateById(bookId, requestDto);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
    }
}

