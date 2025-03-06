package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.UnauthorizedAccessException;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

/**
 * Base controller class that provides common functionality for all API
 * controllers.
 * This includes logging, response handling, and common utility methods.
 */
public abstract class ApiBaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final AuthUtils authUtils;

    /**
     * Constructor for ApiBaseController.
     */
    protected ApiBaseController(AuthUtils authUtils) {
        this.authUtils = authUtils;
    }

    /**
     * Gets the currently logged-in user or throws an exception if not
     * authenticated.
     *
     * @return the User entity of the logged-in user
     * @throws UnauthorizedAccessException if the user is not authenticated
     */
    protected User getLoggedInUser() {
        return authUtils.getCurrentUserOrElseThrow();
    }

    /**
     * Creates a successful response with the given data.
     *
     * @param message the success message
     * @param data    the response data
     * @param <T>     the type of the response data
     * @return ResponseEntity containing the success response
     */
    protected <T> ResponseEntity<ApiResponse<T>> createSuccessResponse(String message, T data) {
        logger.info("Success: {}", message); // INFO log for successful operations
        return ResponseEntity.ok(new ApiResponse<>(true, message, data));
    }

    /**
     * Creates an error response.
     *
     * @param message the error message
     * @param status  the HTTP status
     * @param <T>     the type of the response data
     * @return ResponseEntity containing the error response
     */
    protected <T> ResponseEntity<ApiResponse<T>> createErrorResponse(String message, HttpStatus status) {
        logger.error("Error: {}", message); // ERROR log for failures or critical issues
        return ResponseEntity.status(status).body(new ApiResponse<>(false, message, null));
    }

    /**
     * Creates a bad request response.
     *
     * @param message the error message
     * @param <T>     the type of the response data
     * @return ResponseEntity containing the bad request response
     */
    protected <T> ResponseEntity<ApiResponse<T>> createBadRequestResponse(String message) {
        return createErrorResponse(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates a not found response.
     *
     * @param message the error message
     * @param <T>     the type of the response data
     * @return ResponseEntity containing the not found response
     */
    protected <T> ResponseEntity<ApiResponse<T>> createNotFoundResponse(String message) {
        return createErrorResponse(message, HttpStatus.NOT_FOUND);
    }

    /**
     * Creates a forbidden response.
     *
     * @param message the error message
     * @param <T>     the type of the response data
     * @return ResponseEntity containing the forbidden response
     */
    protected <T> ResponseEntity<ApiResponse<T>> createForbiddenResponse(String message) {
        return createErrorResponse(message, HttpStatus.FORBIDDEN);
    }

    /**
     * Creates an internal server error response.
     *
     * @param message the error message
     * @param <T>     the type of the response data
     * @return ResponseEntity containing the internal server error response
     */
    protected <T> ResponseEntity<ApiResponse<T>> createInternalServerErrorResponse(String message) {
        return createErrorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle service-related errors by providing a generic response.
     * This can be used to catch unexpected service exceptions.
     *
     * @param e       the exception that was thrown
     * @param action  the action being performed (e.g., "create item")
     * @param <T>     the type of the response data
     * @return ResponseEntity containing the error response
     */
    protected <T> ResponseEntity<ApiResponse<T>> handleServiceError(Exception e, String action) {
        logger.error("Failed to {}: {}", action, e.getMessage(), e); // ERROR log for unexpected failures
        return createInternalServerErrorResponse("Failed to " + action);
    }

    /**
     * Checks if the current user is unauthorized to perform an action
     * on the provided userId.
     *
     * @param userId the userId being checked
     * @return true if unauthorized, false otherwise
     */
    protected boolean isUnauthorized(Long userId) {
        User currentUser = getLoggedInUser();
        return !currentUser.getId().equals(userId);
    }

    /**
     * Handle unauthorized access attempts by responding with a forbidden error.
     *
     * @param userId the userId being accessed
     * @param action the action being performed
     * @param <T>    the type of the response data
     * @return ResponseEntity containing the forbidden error response
     */
    protected <T> ResponseEntity<ApiResponse<T>> handleUnauthorizedAccess(Long userId, String action) {
        logger.warn("Unauthorized attempt to {}. Current user ID: {}, Target user ID: {}", action, getLoggedInUser().getId(), userId); // WARN log for unauthorized access
        return createForbiddenResponse("You can only " + action + " your own account");
    }

    /**
     * Handle validation errors by responding with a bad request error.
     *
     * @param result the binding result containing validation errors
     * @param <T>    the type of the response data
     * @return ResponseEntity containing the bad request error response
     */
    protected <T> ResponseEntity<ApiResponse<T>> handleValidationErrors(BindingResult result) {
        // Collecting and formatting validation errors
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        for (ObjectError error : result.getAllErrors()) {
            errorMessage.append(error.getDefaultMessage()).append("; ");
        }

        logger.warn("Validation failed: {}", errorMessage); // WARN log for validation issues
        return createBadRequestResponse(errorMessage.toString());
    }

    /**
     * Custom method for handling ResourceNotFoundException (404).
     *
     * @param <T>     the type of the response data
     * @return ResponseEntity containing the not found response
     */
    protected <T> ResponseEntity<ApiResponse<T>> handleResourceNotFound(ResourceNotFoundException e) {
        logger.error("Resource not found: {}", e.getMessage()); // ERROR log for missing resources
        return createNotFoundResponse("Resource not found: " + e.getMessage());
    }
}
