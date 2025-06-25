package com.products.backend.service.category;

import com.products.backend.model.Category;

import java.util.List;

public interface ICategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    Category createCategory(String name);
    void deleteCategory(Long id);
}
