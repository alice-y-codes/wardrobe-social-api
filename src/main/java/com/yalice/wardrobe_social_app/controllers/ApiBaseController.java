package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.controllers.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.exceptions.UsernameAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Base controller class that provides common functionality for all API
 * controllers.
 */
public abstract class ApiBaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final AuthUtils authUtils;

    protected ApiBaseController(AuthUtils authUtils) {
        this.authUtils = authUtils;
    }

    protected User getLoggedInUser() {
        return authUtils.getCurrentUserOrElseThrow();
    }

    protected <T> ResponseEntity<ApiResponse<T>> handleEntityRetrieval(Supplier<T> supplier, String entityName) {
        try {
            T result = supplier.get();
            logger.info("Successfully retrieved {}: {}", entityName, result);
            logger.info("Success: {} retrieved successfully", entityName);
            return ResponseEntity.ok(new ApiResponse<>(true, entityName + " retrieved successfully", result));
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found while retrieving {}: {}", entityName, e.getMessage());
            logger.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, entityName + " not found", null));
        } catch (SecurityException e) {
            logger.error("Security exception while retrieving {}: {}", entityName, e.getMessage());
            logger.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Unauthorized", null));
        } catch (Exception e) {
            logger.error("Error while retrieving {}: {}", entityName, e.getMessage());
            logger.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve " + entityName, null));
        }
    }

    protected <T> ResponseEntity<ApiResponse<T>> handleEntityAction(
            Supplier<T> action,
            String actionName,
            String entityName,
            String pastTenseAction) {
        try {
            T result = action.get();
            logger.info("Successfully {} {}: {}", actionName, entityName, result);
            logger.info("Success: {} {} successfully", entityName, pastTenseAction);
            return ResponseEntity
                    .ok(new ApiResponse<>(true, entityName + " " + pastTenseAction + " successfully", result));
        } catch (UsernameAlreadyExistsException e) {
            logger.error("Failed to {} {}: {}", actionName, entityName, e.getMessage());
            logger.error("Error: Failed to {} {}", actionName, entityName);
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Username already exists", null));
        } catch (SecurityException e) {
            logger.error("Security exception while trying to {} {}: {}", actionName, entityName, e.getMessage());
            logger.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Unauthorized", null));
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found while trying to {} {}: {}", actionName, entityName, e.getMessage());
            logger.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, entityName + " not found", null));
        } catch (Exception e) {
            logger.error("Error while trying to {} {}: {}", actionName, entityName, e.getMessage());
            logger.error("Error: Failed to {} {}", actionName, entityName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to " + actionName + " " + entityName, null));
        }
    }

    protected ResponseEntity<ApiResponse<Void>> handleVoidAction(
            Runnable action,
            String actionName,
            String entityName,
            String pastTenseAction) {
        try {
            action.run();
            logger.info("Successfully {} {}", actionName, entityName);
            logger.info("Success: {} {} successfully", entityName, pastTenseAction);
            return ResponseEntity
                    .ok(new ApiResponse<>(true, entityName + " " + pastTenseAction + " successfully", null));
        } catch (SecurityException e) {
            logger.error("Security exception while trying to {} {}: {}", actionName, entityName, e.getMessage());
            logger.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Unauthorized", null));
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found while trying to {} {}: {}", actionName, entityName, e.getMessage());
            logger.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, entityName + " not found", null));
        } catch (Exception e) {
            logger.error("Error while trying to {} {}: {}", actionName, entityName, e.getMessage());
            logger.error("Error: Failed to {} {}", actionName, entityName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to " + actionName + " " + entityName, null));
        }
    }

    protected <T> ResponseEntity<ApiResponse<T>> handleValidationErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        String errorMessage = errors.values().iterator().next(); // Get the first error message
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, errorMessage, null));
    }

    // ========== RESPONSE HELPERS ==========
    protected <T> ResponseEntity<ApiResponse<T>> createSuccessResponse(String message, T data) {
        logger.info("Success: {}", message);
        return ResponseEntity.ok(new ApiResponse<>(true, message, data));
    }

    protected <T> ResponseEntity<ApiResponse<T>> createErrorResponse(String message, HttpStatus status) {
        logger.error("Error: {}", message);
        return ResponseEntity.status(status).body(new ApiResponse<>(false, message, null));
    }

    protected <T> ResponseEntity<ApiResponse<T>> createBadRequestResponse(String message) {
        return createErrorResponse(message, HttpStatus.BAD_REQUEST);
    }

    protected <T> ResponseEntity<ApiResponse<T>> createNotFoundResponse(String message) {
        return createErrorResponse(message, HttpStatus.NOT_FOUND);
    }

    protected <T> ResponseEntity<ApiResponse<T>> createForbiddenResponse(String message) {
        return createErrorResponse(message, HttpStatus.FORBIDDEN);
    }

    protected <T> ResponseEntity<ApiResponse<T>> createInternalServerErrorResponse(String message) {
        return createErrorResponse(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    protected <T> ResponseEntity<ApiResponse<T>> createUnauthorizedResponse(String message) {
        return createErrorResponse(message, HttpStatus.UNAUTHORIZED);
    }

    // ========== ERROR HANDLING ==========
    protected <T> ResponseEntity<ApiResponse<T>> handleResourceNotFound(ResourceNotFoundException e) {
        logger.error("Resource not found: {}", e.getMessage());
        return createNotFoundResponse("Resource not found");
    }

    protected <T> ResponseEntity<ApiResponse<T>> handleServiceError(Exception e, String action) throws Exception {
        logger.error("Failed to {}: {}", action, e.getMessage(), e);
        if (e instanceof ResourceNotFoundException || e instanceof SecurityException) {
            throw e;
        }
        return createInternalServerErrorResponse("Failed to " + action);
    }

    // ========== AUTHORIZATION HELPERS ==========
    protected boolean isUnauthorized(Long userId) {
        User currentUser = getLoggedInUser();
        return !currentUser.getId().equals(userId);
    }

    protected <T> ResponseEntity<ApiResponse<T>> handleUnauthorizedAccess(Long userId, String action) {
        logger.warn("Unauthorized attempt to {}. Current user ID: {}, Target user ID: {}", action,
                getLoggedInUser().getId(), userId);
        return createForbiddenResponse("Unauthorized");
    }

    @FunctionalInterface
    public interface EntitySupplier<T> {
        T get() throws Exception; // A method that can throw an exception
    }

}
