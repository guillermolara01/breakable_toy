package com.products.backend.repository;

import com.products.backend.model.Category;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CategoryRepository {
    private final Map<Long, Category> categoryRepository = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);



    public List<Category> findAll() {
        return List.copyOf(categoryRepository.values());
    }

    public Category findById(Long id){
        return categoryRepository.get(id);
    }

    public Category save(String name){
        Category category = new Category();
        category.setId(idGenerator.incrementAndGet());
        category.setName(name);
        categoryRepository.put(category.getId(), category);
        return category;
    }

    public void delete(Long id){
        categoryRepository.remove(id);
    }

    public void setPlaceHolders(){
        save("Food");
        save("Cleaning");
        save("Pharmacy");
        save("Electronics");
    }

}
