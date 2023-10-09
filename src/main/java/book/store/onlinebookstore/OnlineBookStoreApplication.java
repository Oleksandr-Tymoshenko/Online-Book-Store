package book.store.onlinebookstore;

import book.store.onlinebookstore.model.Book;
import book.store.onlinebookstore.service.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OnlineBookStoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book newBook = new Book();
            newBook.setTitle("Book title");
            newBook.setAuthor("Author");
            newBook.setIsbn("af-1dds-13sd");
            newBook.setPrice(BigDecimal.valueOf(100));
            bookService.save(newBook);
            System.out.println(bookService.findAll());
        };
    }
}
