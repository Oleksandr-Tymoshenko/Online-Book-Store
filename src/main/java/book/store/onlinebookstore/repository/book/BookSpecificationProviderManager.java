package book.store.onlinebookstore.repository.book;

import book.store.onlinebookstore.exception.SpecificationNotFoundException;
import book.store.onlinebookstore.model.Book;
import book.store.onlinebookstore.repository.SpecificationProvider;
import book.store.onlinebookstore.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private final List<SpecificationProvider<Book>> specificationProviderList;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return specificationProviderList.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() ->
                        new SpecificationNotFoundException("Can't get specification by key "
                                + key));
    }
}
