package com.products.backend.service.category;


import com.products.backend.exception.CategoryInUseException;
import com.products.backend.exception.DuplicateResourceException;
import com.products.backend.exception.ResourceNotFoundException;
import com.products.backend.exception.ValidationException;
import com.products.backend.model.Category;
import com.products.backend.repository.CategoryRepository;
import com.products.backend.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Refactored CategoryService with proper exception handling
 */
@Service
public class CategoryService implements ICategoryService {
    private final CategoryRepository categories;
    private final ProductRepository products;

    public CategoryService(CategoryRepository categories, ProductRepository products) {
        this.categories = categories;
        this.products = products;
    }

    @PostConstruct
    public void initPlaceholders() {
        categories.setPlaceHolders();
    }

    @Override
    public Category createCategory(String name) {
        // Validate name
        if (name == null || name.isBlank()) {
            throw new ValidationException("Category name cannot be empty");
        }

        // Check for duplicate category name
        boolean exists = categories.findAll().stream()
                .anyMatch(cat -> cat.getName().equalsIgnoreCase(name.trim()));

        if (exists) {
            throw new DuplicateResourceException("Category", "name", name);
        }

        return categories.save(name.trim());
    }

    @Override
    public Category getCategoryById(Long id) {
        Category category = categories.findById(id);

        if (category == null) {
            throw new ResourceNotFoundException("Category", id);
        }

        return category;
    }

    @Override
    public void deleteCategory(Long id) {
        // Check if category exists
        Category category = categories.findById(id);
        if (category == null) {
            throw new ResourceNotFoundException("Category", id);
        }

        // Check if category is being used by any products
        long productCount = products.findAll().stream()
                .filter(product -> product.getCategory() != null &&
                        product.getCategory().getId() == id)
                .count();

        if (productCount > 0) {
            throw new CategoryInUseException(id, (int) productCount);
        }

        categories.delete(id);
    }

    @Override
    public List<Category> getAllCategories() {
        return categories.findAll();
    }
}