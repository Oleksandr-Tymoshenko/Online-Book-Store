package book.store.onlinebookstore.repository.book;

import book.store.onlinebookstore.dto.BookSearchParameters;
import book.store.onlinebookstore.model.Book;
import book.store.onlinebookstore.repository.SpecificationBuilder;
import book.store.onlinebookstore.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> specificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> specification = Specification.where(null);
        if (searchParameters.titles() != null && searchParameters.titles().length > 0) {
            specification = specification
                    .and(specificationProviderManager
                            .getSpecificationProvider("title")
                            .getSpecification(searchParameters.titles()));
        }
        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            specification = specification
                    .and(specificationProviderManager
                            .getSpecificationProvider("author")
                            .getSpecification(searchParameters.authors()));
        }
        return specification;
    }
}
