package book.store.onlinebookstore.repository.book.spec;

import book.store.onlinebookstore.model.Book;
import book.store.onlinebookstore.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "title";
    }

    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get("title")
                .in(Arrays.stream(params).toList());
    }
}
