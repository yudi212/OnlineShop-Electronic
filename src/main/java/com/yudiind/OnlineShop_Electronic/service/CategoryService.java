package com.yudiind.OnlineShop_Electronic.service;

import com.yudiind.OnlineShop_Electronic.model.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {

    CategoryDTO saveCategory(CategoryDTO categoryDTO);

    CategoryDTO updateCategory(CategoryDTO categoryDTO);

    CategoryDTO getCategoryById(Long id);

    List<CategoryDTO> getAllCategory();

    void deleteCategoryById(Long id);
}
