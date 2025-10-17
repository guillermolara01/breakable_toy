package com.products.backend.exception;

/**
 * Thrown when validation fails (400)
 */
public class ValidationException extends ApplicationException {
    public ValidationException(String message) {
        super(message);
    }
}
