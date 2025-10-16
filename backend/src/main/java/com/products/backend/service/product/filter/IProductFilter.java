package com.products.backend.service.product.filter;

import com.products.backend.model.Product;
import java.util.function.Predicate;

/**
 * Strategy interface for filtering products
 */
public interface IProductFilter {
    Predicate<Product> buildPredicate();
}