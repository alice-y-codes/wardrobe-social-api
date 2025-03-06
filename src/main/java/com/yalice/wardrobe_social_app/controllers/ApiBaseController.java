package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.UnauthorizedAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
        logger.info("Success: {}", message);
        return ResponseEntity.ok(new ApiResponse<>(true, message, data));
    }

    /**
     * Creates a successful response with the given data and HTTP status.
     *
     * @param message the success message
     * @param data    the response data
     * @param status  the HTTP status
     * @param <T>     the type of the response data
     * @return ResponseEntity containing the success response
     */
    protected <T> ResponseEntity<ApiResponse<T>> createSuccessResponse(String message, T data, HttpStatus status) {
        logger.info("Success: {}", message);
        return ResponseEntity.status(status).body(new ApiResponse<>(true, message, data));
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
        logger.error("Error: {}", message);
        return ResponseEntity.status(status).body(new ApiResponse<T>(false, message, null));
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
}
