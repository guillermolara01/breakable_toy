package com.products.backend.repository;

import com.products.backend.model.Product;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ProductRepository {

    private final Map<Long, Product> productStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public List<Product> findAll() {
        return new ArrayList<>(productStore.values());
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(productStore.get(id));
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idGenerator.incrementAndGet());
        }
        productStore.put(product.getId(), product);
        return product;
    }

    public void delete(Long id) {
        productStore.remove(id);
    }

    public void clear() {
        productStore.clear();
    }
}
