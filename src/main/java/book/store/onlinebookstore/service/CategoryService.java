package book.store.onlinebookstore.service;

import book.store.onlinebookstore.dto.category.CategoryDto;
import book.store.onlinebookstore.dto.category.CreateCategoryRequestDto;
import book.store.onlinebookstore.dto.category.UpdateCategoryRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto getById(Long id);

    CategoryDto save(CreateCategoryRequestDto categoryRequestDto);

    CategoryDto update(Long id, UpdateCategoryRequestDto categoryRequestDto);

    void deleteById(Long id);
}
