package com.yudiind.OnlineShop_Electronic.controller;


import com.yudiind.OnlineShop_Electronic.model.dto.CategoryDTO;
import com.yudiind.OnlineShop_Electronic.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveCategory(@RequestBody @Valid CategoryDTO categoryDTO,
                                                    BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError ->
                    errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

       CategoryDTO category = categoryService.saveCategory(categoryDTO);
       return ResponseEntity.ok(category);
    }

    @PutMapping("/update")
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO categoryDTO){
        CategoryDTO category = categoryService.updateCategory(categoryDTO);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id){
        CategoryDTO category = categoryService.getCategoryById(id);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryDTO>> getAllCategory(){
        List<CategoryDTO> category = categoryService.getAllCategory();
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteById(@PathVariable Long id){
        categoryService.deleteCategoryById(id);;
    }

}
