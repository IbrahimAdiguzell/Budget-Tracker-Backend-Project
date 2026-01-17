package com.financeproject.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financeproject.entity.Category;
import com.financeproject.repository.ICategoryRepository;
// Import our helper class to ensure consistency
import com.financeproject.util.CategoryHelper; 

@RestController // REST API Gateway
@RequestMapping("/api/categories") // Base URL: localhost:8080/api/categories
public class CategoryController {

    private final ICategoryRepository categoryRepository;

    public CategoryController(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // 1. GET ALL CATEGORIES (GET Request)
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 2. ADD NEW CATEGORY (POST Request)
    @PostMapping
    public Category addNewCategory(@RequestBody Category newCategory) {
        // PROFESSIONAL TOUCH:
        // Automatically assign an icon if the user didn't provide one.
        if (newCategory.getIcon() == null || newCategory.getIcon().isEmpty()) {
            String icon = CategoryHelper.getIconForName(newCategory.getName());
            newCategory.setIcon(icon);
        }
        
        return categoryRepository.save(newCategory);
    }
}