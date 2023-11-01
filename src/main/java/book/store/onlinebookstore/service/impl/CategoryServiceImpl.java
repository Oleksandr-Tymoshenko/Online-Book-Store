package book.store.onlinebookstore.service.impl;

import book.store.onlinebookstore.dto.category.CategoryDto;
import book.store.onlinebookstore.dto.category.CreateCategoryRequestDto;
import book.store.onlinebookstore.exception.EntityNotFoundException;
import book.store.onlinebookstore.mapper.CategoryMapper;
import book.store.onlinebookstore.model.Category;
import book.store.onlinebookstore.repository.category.CategoryRepository;
import book.store.onlinebookstore.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll().stream().map(categoryMapper::toDto).toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        return categoryMapper.toDto(categoryRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category was not fount by id "
                        + id)));
    }

    @Override
    public CategoryDto save(CreateCategoryRequestDto categoryRequestDto) {
        return categoryMapper.toDto(categoryRepository
                .save(categoryMapper.toCategory(categoryRequestDto)));
    }

    @Override
    public CategoryDto update(Long id, CreateCategoryRequestDto categoryRequestDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find category by id " + id));
        categoryMapper.updateCategory(categoryRequestDto, category);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
