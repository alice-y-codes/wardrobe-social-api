package com.yalice.wardrobe_social_app.exceptions;

import com.yalice.wardrobe_social_app.controllers.utilities.ApiResponse;
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
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleUserRegistrationException(
            final UserRegistrationException e) {
        HttpStatus status = "Username already taken".equals(e.getMessage()) ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;
        Map<String, String> errorResponse = Map.of("message", e.getMessage());
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(false, e.getMessage(), errorResponse);
        return ResponseEntity.status(status).body(apiResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            final MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(false, "Validation failed", errors);
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleIllegalArgumentException(
            final IllegalArgumentException ex) {
        Map<String, String> errorResponse = Map.of("message", "Invalid argument provided", "details", ex.getMessage());
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(false, "Bad request", errorResponse);
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleNoSuchElementException(
            final NoSuchElementException ex) {
        Map<String, String> errorResponse = Map.of("message", "Resource not found", "details", ex.getMessage());
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(false, "Not Found", errorResponse);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException ex) {
        Map<String, String> errorResponse = Map.of("message", "Invalid request body", "details", ex.getMessage());
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(false, "Bad request", errorResponse);
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentTypeMismatchException(
            final MethodArgumentTypeMismatchException ex) {
        Map<String, String> errorResponse = Map.of(
                "message", "Invalid parameter type",
                "details",
                "Parameter '" + ex.getName() + "' should be of type " + ex.getRequiredType().getSimpleName());
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(false, "Bad request", errorResponse);
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMissingServletRequestParameterException(
            final MissingServletRequestParameterException ex) {
        Map<String, String> errorResponse = Map.of(
                "message", "Missing required parameter",
                "details",
                "Parameter '" + ex.getParameterName() + "' of type " + ex.getParameterType() + " is required");
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(false, "Bad request", errorResponse);
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleDataAccessException(
            final DataAccessException ex) {
        Map<String, String> errorResponse = Map.of("message", "Database error occurred", "details", ex.getMessage());
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(false, "Internal Server Error", errorResponse);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        Map<String, String> errorResponse = Map.of("message", "Resource not found", "details", ex.getMessage());
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(false, "Not Found", errorResponse);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiResponse<String>> handleUnauthorizedAccess(UnauthorizedAccessException ex) {
        ApiResponse<String> apiResponse = new ApiResponse<>(false, ex.getMessage(), ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<String>> handleSecurityException(SecurityException ex) {
        ApiResponse<String> apiResponse = new ApiResponse<>(false, ex.getMessage(), ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<String> handleMissingPartException(MissingServletRequestPartException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required file: " + ex.getRequestPartName());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleAllOtherExceptions(final Exception ex) {
        ex.printStackTrace();
        Map<String, String> errorResponse = Map.of(
                "message", "An unexpected error occurred",
                "details", ex.getMessage(),
                "exceptionType", ex.getClass().getName());
        ApiResponse<Map<String, String>> apiResponse = new ApiResponse<>(false, "Internal Server Error", errorResponse);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
}
