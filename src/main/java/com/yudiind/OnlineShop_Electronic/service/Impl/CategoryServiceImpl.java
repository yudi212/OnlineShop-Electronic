package com.yudiind.OnlineShop_Electronic.service.Impl;

import com.yudiind.OnlineShop_Electronic.error.exception.ResourceNotFoundException;
import com.yudiind.OnlineShop_Electronic.model.dto.CategoryDTO;
import com.yudiind.OnlineShop_Electronic.model.entity.Category;
import com.yudiind.OnlineShop_Electronic.repository.CategoryRepository;
import com.yudiind.OnlineShop_Electronic.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {


    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setName(categoryDTO.getName());

        Category savedCategory = categoryRepository.save(category);
        return CategoryDTO.builder()
                .id(savedCategory.getId())
                .name(savedCategory.getName())
                .build();
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO) {
       Category category = categoryRepository.findById(categoryDTO.getId())
                .orElseThrow(()-> new ResourceNotFoundException("category not found"));

       category.setName(categoryDTO.getName());
       Category updatedCategory = categoryRepository.save(category);
       return CategoryDTO.builder()
               .id(updatedCategory.getId())
               .name(updatedCategory.getName())
               .build();

    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (!category.isPresent()){
            return null;
        }
        return new CategoryDTO(category.get().getId(), category.get().getName());
    }

    @Override
    public List<CategoryDTO> getAllCategory(){
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()){
            throw new ResourceNotFoundException("Could not find product categories");
        }

        // untuk mengonversi setiap Category menjadi CategoryDTO
        return categories.stream()
                .map(category -> CategoryDTO.builder()
                                .id(category.getId())
                                .name(category.getName())
                                .build())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);

    }
}
