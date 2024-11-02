package com.yudiind.OnlineShop_Electronic.service;

import com.yudiind.OnlineShop_Electronic.model.dto.CategoryDTO;
import com.yudiind.OnlineShop_Electronic.model.entity.Category;
import com.yudiind.OnlineShop_Electronic.repository.CategoryRepository;
import com.yudiind.OnlineShop_Electronic.service.Impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void testSavedCategory() {

        CategoryDTO categoryDTO = new CategoryDTO(1L, "smartphone");
        Category category = new Category(1L, "smartphone");

        when(categoryRepository.save(any())).thenReturn(category);

        CategoryDTO savedCategory = categoryService.saveCategory(categoryDTO);

        assertNotNull(savedCategory);
        assertEquals(savedCategory.getId(), category.getId());
        assertEquals(savedCategory.getName(), category.getName());

        verify(categoryRepository, times(1)).save(any());
    }

    @Test
    void testUpdatedCategory() {

        CategoryDTO categoryDTO = new CategoryDTO(1L, "new smartphone");
        Category updatedCategory = new Category(1L, "new smartphone");

        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("smartphone");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(updatedCategory);

        CategoryDTO result = categoryService.updateCategory(categoryDTO);

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(existingCategory);

        assertNotNull(result);
        assertEquals(result.getId(), categoryDTO.getId());
        assertEquals(result.getName(), categoryDTO.getName());
    }

    @Test
    void testGetCategoryById() {

        Long categoryId = 1L;
        Category category = new Category(categoryId, "smartphone");
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        CategoryDTO result = categoryService.getCategoryById(category.getId());

        verify(categoryRepository,times(1)).findById(categoryId);

        assertNotNull(result);
        assertEquals(result.getId(), category.getId());
        assertEquals(result.getName(), category.getName());
    }

    @Test
    void testGetAllCategory() {

        String categoryName = "smartphone";
        Category category = new Category();
        category.setName(categoryName);

        List<Category> categoryList = Stream.generate(() -> category)
                .limit(3)
                .collect(Collectors.toList());

        when(categoryRepository.findAll()).thenReturn(categoryList);

        List<CategoryDTO> result = categoryService.getAllCategory();

        assertEquals(result.size(), categoryList.size());
        result.forEach(resultDTO ->
                assertEquals(resultDTO.getName(), category.getName()));
    }

    @Test
    void testDeleteCategoryById() {

        Long categoryId = 1L;

        assertDoesNotThrow(()-> categoryRepository.deleteById(categoryId));

        verify(categoryRepository, times(1)).deleteById(categoryId);;
    }
}
