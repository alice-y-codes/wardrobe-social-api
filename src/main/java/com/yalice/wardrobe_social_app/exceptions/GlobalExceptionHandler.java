package com.yalice.wardrobe_social_app.exceptions;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Global exception handler for the application that handles various types of
 * exceptions
 * and converts them into appropriate HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles user registration related exceptions.
     *
     * @param e The UserRegistrationException that was thrown
     * @return ResponseEntity containing the error message and appropriate status
     *         code
     */
    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<?> handleUserRegistrationException(
            final UserRegistrationException e) {
        if ("Username already taken".equals(e.getMessage())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
        return ResponseEntity.badRequest()
                .body(Map.of("message", e.getMessage()));
    }

    /**
     * Handles validation exceptions from method arguments.
     *
     * @param ex The MethodArgumentNotValidException that was thrown
     * @return ResponseEntity containing validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(
            final MethodArgumentNotValidException ex) {
        final Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            final String fieldName = ((FieldError) error).getField();
            final String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles illegal argument exceptions.
     *
     * @param ex The IllegalArgumentException that was thrown
     * @return ResponseEntity containing the error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(
            final IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "message", "Invalid argument provided",
                "details", ex.getMessage()));
    }

    /**
     * Handles no such element exceptions.
     *
     * @param ex The NoSuchElementException that was thrown
     * @return ResponseEntity containing the error message
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementException(
            final NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "message", "Resource not found",
                "details", ex.getMessage()));
    }

    /**
     * Handles HTTP message not readable exceptions.
     *
     * @param ex The HttpMessageNotReadableException that was thrown
     * @return ResponseEntity containing the error message
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "message", "Invalid request body",
                "details", ex.getMessage()));
    }

    /**
     * Handles method argument type mismatch exceptions.
     *
     * @param ex The MethodArgumentTypeMismatchException that was thrown
     * @return ResponseEntity containing the error message
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(
            final MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "message", "Invalid parameter type",
                "details",
                "Parameter '" + ex.getName() + "' should be of type "
                        + ex.getRequiredType().getSimpleName()));
    }

    /**
     * Handles missing servlet request parameter exceptions.
     *
     * @param ex The MissingServletRequestParameterException that was thrown
     * @return ResponseEntity containing the error message
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameterException(
            final MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "message", "Missing required parameter",
                "details",
                "Parameter '" + ex.getParameterName() + "' of type "
                        + ex.getParameterType() + " is required"));
    }

    /**
     * Handles data access exceptions.
     *
     * @param ex The DataAccessException that was thrown
     * @return ResponseEntity containing the error message
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleDataAccessException(
            final DataAccessException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "message", "Database error occurred",
                        "details", ex.getMessage()));
    }

    /**
     * Handles all other exceptions that are not handled by specific handlers.
     *
     * @param ex The Exception that was thrown
     * @return ResponseEntity containing the error message and exception details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllOtherExceptions(final Exception ex) {
        // Log the exception for debugging
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "message", "An unexpected error occurred",
                        "details", ex.getMessage(),
                        "exceptionType", ex.getClass().getName()));
    }
}
