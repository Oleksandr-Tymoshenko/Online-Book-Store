package book.store.onlinebookstore.repository.impl;

import book.store.onlinebookstore.exception.DataProcessingException;
import book.store.onlinebookstore.exception.EntityNotFoundException;
import book.store.onlinebookstore.model.Book;
import book.store.onlinebookstore.repository.BookRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {
    private final SessionFactory sessionFactory;

    @Override
    public Book save(Book book) {
        try {
            sessionFactory.inTransaction(session -> session.persist(book));
        } catch (Exception e) {
            throw new DataProcessingException("Can't save book to db", e);
        }
        return book;
    }

    @Override
    public Optional<Book> findById(Long id) {
        try {
            return Optional.ofNullable(sessionFactory.openSession().find(Book.class, id));
        } catch (Exception e) {
            throw new EntityNotFoundException("Can't find book by id " + id, e);
        }

    }

    @Override
    public List<Book> findAll() {
        try {
            return sessionFactory
                    .openSession().createQuery("from Book ", Book.class).getResultList();
        } catch (Exception e) {
            throw new DataProcessingException("Can't receive all books from db", e);
        }
    }
}
