package book.store.onlinebookstore.repository.impl;

import book.store.onlinebookstore.model.Book;
import book.store.onlinebookstore.repository.BookRepository;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepositoryImpl implements BookRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public BookRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Book save(Book book) {
        sessionFactory.inTransaction(session -> session.persist(book));
        return book;
    }

    @Override
    public List<Book> findAll() {
        return sessionFactory.openSession().createQuery("from Book ", Book.class).getResultList();
    }
}
