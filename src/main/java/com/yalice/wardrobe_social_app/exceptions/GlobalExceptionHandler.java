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

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleUsernameAlreadyExistsException(
            final UsernameAlreadyExistsException e) {
        ApiResponse<Void> apiResponse = new ApiResponse<>(false, e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
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
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            final IllegalArgumentException ex) {
        ApiResponse<Void> apiResponse = new ApiResponse<>(false, ex.getMessage(), null);
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoSuchElementException(
            final NoSuchElementException ex) {
        ApiResponse<Void> apiResponse = new ApiResponse<>(false, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException ex) {
        ApiResponse<Void> apiResponse = new ApiResponse<>(false, "Invalid request body", null);
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(
            final MethodArgumentTypeMismatchException ex) {
        String message = String.format("Parameter '%s' should be of type %s",
                ex.getName(), ex.getRequiredType().getSimpleName());
        ApiResponse<Void> apiResponse = new ApiResponse<>(false, message, null);
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(
            final MissingServletRequestParameterException ex) {
        String message = String.format("Parameter '%s' of type %s is required",
                ex.getParameterName(), ex.getParameterType());
        ApiResponse<Void> apiResponse = new ApiResponse<>(false, message, null);
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccessException(
            final DataAccessException ex) {
        ApiResponse<Void> apiResponse = new ApiResponse<>(false, "Database error occurred", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        ApiResponse<Void> apiResponse = new ApiResponse<>(false, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedAccess(UnauthorizedAccessException ex) {
        ApiResponse<Void> apiResponse = new ApiResponse<>(false, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<Void>> handleSecurityException(SecurityException ex) {
        ApiResponse<Void> apiResponse = new ApiResponse<>(false, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingPartException(MissingServletRequestPartException ex) {
        ApiResponse<Void> apiResponse = new ApiResponse<>(false, ex.getMessage(), null);
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllOtherExceptions(final Exception ex) {
        ex.printStackTrace();
        ApiResponse<Void> apiResponse = new ApiResponse<>(false, "An unexpected error occurred", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
}
