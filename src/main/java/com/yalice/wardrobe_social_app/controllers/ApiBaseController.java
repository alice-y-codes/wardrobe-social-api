package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.controllers.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RestController;

/**
 * Base controller class that provides common functionality for all API
 * controllers.
 */
@RestController
public abstract class ApiBaseController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final AuthUtils authUtils;

    protected ApiBaseController(AuthUtils authUtils) {
        this.authUtils = authUtils;
    }

    protected User getLoggedInUser() {
        return authUtils.getCurrentUserOrElseThrow();
    }

    // ========== GENERIC HANDLERS ==========
    protected ResponseEntity<ApiResponse<Void>> handleVoidAction(VoidSupplier supplier, String action,
            String entityName, String pastAction) {
        try {
            supplier.execute();
            logger.info("Successfully {} {}", action, entityName);
            return createSuccessResponse(entityName + " " + pastAction + " successfully", null);
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found while trying to {} {}: {}", action, entityName, e.getMessage());
            return createNotFoundResponse(e.getMessage());
        } catch (SecurityException e) {
            logger.error("Security exception while trying to {} {}: {}", action, entityName, e.getMessage());
            return createUnauthorizedResponse(e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to {} {}: {}", action, entityName, e.getMessage(), e);
            return createInternalServerErrorResponse("Failed to " + action + " " + entityName);
        }
    }

    protected <T> ResponseEntity<ApiResponse<T>> handleEntityAction(EntitySupplier<T> supplier, String action,
            String entityName, String pastAction) {
        try {
            T result = supplier.get();
            logger.info("Successfully {}d {}: {}", action, entityName, result);
            return createSuccessResponse(entityName + " " + pastAction + " successfully", result);
        } catch (ResourceNotFoundException e) {
            logger.error("Resource not found while trying to {} {}: {}", action, entityName, e.getMessage());
            return createNotFoundResponse(e.getMessage());
        } catch (SecurityException e) {
            logger.error("Security exception while trying to {} {}: {}", action, entityName, e.getMessage());
            return createUnauthorizedResponse(e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to {} {}: {}", action, entityName, e.getMessage(), e);
            return createInternalServerErrorResponse("Failed to " + action + " " + entityName);
        }
    }

    protected <T> ResponseEntity<ApiResponse<T>> handleEntityRetrieval(EntitySupplier<T> supplier, String entityName) {
        return handleEntityAction(supplier, "retrieve", entityName, "retrieved");
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
    protected <T> ResponseEntity<ApiResponse<T>> handleValidationErrors(BindingResult result) {
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        for (ObjectError error : result.getAllErrors()) {
            errorMessage.append(error.getDefaultMessage()).append("; ");
        }
        logger.warn("Validation failed: {}", errorMessage);
        return createBadRequestResponse(errorMessage.toString());
    }

    protected <T> ResponseEntity<ApiResponse<T>> handleResourceNotFound(ResourceNotFoundException e) {
        logger.error("Resource not found: {}", e.getMessage());
        return createNotFoundResponse("Resource not found: " + e.getMessage());
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
        return createForbiddenResponse("You do not have permission to " + action + " this resource.");

    }

    // ========== FUNCTIONAL INTERFACES ==========
    @FunctionalInterface
    public interface EntitySupplier<T> {
        T get() throws Exception;
    }

    @FunctionalInterface
    public interface VoidSupplier {
        void execute() throws Exception;
    }
}
