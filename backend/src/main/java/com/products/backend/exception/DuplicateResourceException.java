package com.products.backend.exception;

/**
 * Thrown when trying to create a duplicate resource (409)
 */
public class DuplicateResourceException extends ApplicationException {
    public DuplicateResourceException(String resource, String field, Object value) {
        super(String.format("%s already exists with %s: %s", resource, field, value));
    }
}