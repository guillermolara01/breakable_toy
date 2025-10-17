package com.products.backend.exception;

/**
 * Thrown when category cannot be deleted due to existing products (409)
 */
public class CategoryInUseException extends ApplicationException {
    public CategoryInUseException(Long categoryId, int productCount) {
        super(String.format("Cannot delete category with id %d: %d product(s) are using this category",
                categoryId, productCount));
    }
}