package book.store.onlinebookstore.service;

import book.store.onlinebookstore.model.Book;
import java.util.List;

public interface BookService {
    Book save(Book book);

    List<Book> findAll();
}
