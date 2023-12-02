package book.store.onlinebookstore.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import book.store.onlinebookstore.dto.category.CategoryDto;
import book.store.onlinebookstore.dto.category.CreateCategoryRequestDto;
import book.store.onlinebookstore.dto.category.UpdateCategoryRequestDto;
import book.store.onlinebookstore.exception.EntityNotFoundException;
import book.store.onlinebookstore.mapper.CategoryMapper;
import book.store.onlinebookstore.model.Category;
import book.store.onlinebookstore.repository.category.CategoryRepository;
import book.store.onlinebookstore.service.impl.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Check if findAll categories works")
    void findAll_ValidPageable_ReturnsAllCategories() {
        //given

        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Test category 1");
        CategoryDto categoryDto1 = new CategoryDto(
                category1.getId(),
                category1.getName(),
                category1.getDescription());

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Test category 2");
        CategoryDto categoryDto2 = new CategoryDto(
                category2.getId(),
                category2.getName(),
                category2.getDescription());
        List<Category> categories = List.of(category1, category2);
        List<CategoryDto> expected = List.of(categoryDto1, categoryDto2);
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(new PageImpl<>(categories));
        Mockito.when(categoryMapper.toDto(category1))
                .thenReturn(categoryDto1);
        Mockito.when(categoryMapper.toDto(category2))
                .thenReturn(categoryDto2);
        //when
        List<CategoryDto> actual = categoryService.findAll(pageable);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
        Mockito.verify(categoryRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Check if find category by id returns category dto")
    void getById_ValidId_ReturnsCategoryDto() {
        //given
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Test category");

        CategoryDto expected = new CategoryDto(
                categoryId,
                category.getName(),
                category.getDescription());

        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        Mockito.when(categoryMapper.toDto(category)).thenReturn(expected);

        //when
        CategoryDto actual = categoryService.getById(categoryId);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
        Mockito.verify(categoryRepository, Mockito.times(1))
                .findById(categoryId);
    }

    @Test
    @DisplayName("Check if get category by id throws exception with incorrect id")
    void getById_InvalidId_ThrowsException() {
        //given
        Long categoryId = -1L;
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        //when
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.getById(categoryId)
        );

        //then
        String expected = "Category was not fount by id " + categoryId;
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Check if category is saved and correct dto is returned")
    void save_ValidCreateCategoryRequest_ReturnsCategoryWithId() {
        //given
        var categoryRequestDto = new CreateCategoryRequestDto("Category name", null);
        Category category = new Category();
        category.setId(1L);
        category.setName(categoryRequestDto.name());
        CategoryDto expected = new CategoryDto(category.getId(), category.getName(), null);
        Mockito.when(categoryMapper.toCategory(categoryRequestDto)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(expected);

        //when
        CategoryDto actual = categoryService.save(categoryRequestDto);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(category);
    }

    @Test
    @DisplayName("Check if updateById category returns updated categoryDto")
    void updateById_ValidId_ReturnsUpdatedCategoryDto() {
        //given
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Old name");
        var categoryRequestDto = new UpdateCategoryRequestDto("Updated name", null);

        CategoryDto expected = new CategoryDto(categoryId, categoryRequestDto.name(), null);

        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(expected);

        //when
        CategoryDto actual = categoryService.update(categoryId, categoryRequestDto);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(category);
    }

    @Test
    @DisplayName("Check if deleteById category throws exception with incorrect index")
    void deleteById_InvalidId_ThrowsException() {
        //given
        Long categoryId = -1L;

        //when
        IndexOutOfBoundsException exception = assertThrows(
                IndexOutOfBoundsException.class,
                () -> categoryService.deleteById(categoryId)
        );

        //then
        String expected = "Index cannot be less then 1";
        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Check if delete category is invoked")
    void deleteById_ValidId_DeletesCategory() {
        //given
        Long categoryId = 1L;

        //when
        categoryService.deleteById(categoryId);

        //then
        Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(categoryId);
    }
}
