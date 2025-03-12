package com.yalice.wardrobe_social_app.exceptions;

/**
 * Exception thrown when validation fails for an entity or operation.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}