package com.products.backend.exception;

/**
 * Thrown when a business logic rule is violated (400)
 */
public class InvalidOperationException extends ApplicationException {
    public InvalidOperationException(String message) {
        super(message);
    }
}