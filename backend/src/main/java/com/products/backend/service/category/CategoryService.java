package com.products.backend.service.category;


import com.products.backend.model.Category;
import com.products.backend.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService implements ICategoryService {
    private final CategoryRepository categories;

    public CategoryService(CategoryRepository categories) {
        this.categories = categories;
    }


    @PostConstruct
    public void initPlaceholders() {
        categories.setPlaceHolders();
    }

    @Override
    public Category createCategory(String name){
        return this.categories.save(name);
    }

    @Override
    public Category getCategoryById(Long id){
        return this.categories.findById(id);
    }

    @Override
    public void deleteCategory(Long id){
        this.categories.delete(id);
    }
    @Override
    public List<Category> getAllCategories(){
        return categories.findAll();
    }
}
