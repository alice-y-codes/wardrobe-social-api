package com.yalice.wardrobe_social_app.exceptions;

/**
 * Exception thrown when user registration fails due to validation errors
 * or other registration-related issues.
 */
public class UserRegistrationException extends RuntimeException {

    /**
     * Constructs a new UserRegistrationException with the specified message.
     *
     * @param message The error message describing the registration failure
     */
    public UserRegistrationException(final String message) {
        super(message);
    }
}
