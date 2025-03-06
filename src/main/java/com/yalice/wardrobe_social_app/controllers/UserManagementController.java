package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.controllers.helpers.UserDtoValidator;
import com.yalice.wardrobe_social_app.dtos.user.UserDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.UserRegistrationException;
import com.yalice.wardrobe_social_app.interfaces.UserManagementService;
import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling user management operations.
 * Provides endpoints for user registration, profile updates, and account
 * management.
 */
@RestController
@RequestMapping("/api/users")
public class UserManagementController extends ApiBaseController {

    private final UserManagementService userManagementService;
    private final UserDtoValidator userDtoValidator;

    @Autowired
    public UserManagementController(UserManagementService userManagementService, AuthUtils authUtils) {
        super(authUtils);
        this.userManagementService = userManagementService;
        this.userDtoValidator = new UserDtoValidator();
    }

    /**
     * Registers a new user in the system.
     *
     * @param userDto the user registration data
     * @param result  the binding result for validation
     * @return ResponseEntity containing the registered user or validation errors
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> registerUser(@RequestBody UserDto userDto,
            BindingResult result) {
        logger.info("Attempting to register new user with username: {}", userDto.getUsername());

        userDtoValidator.validate(userDto, result);
        if (result.hasErrors()) {
            logger.warn("Validation failed for user registration: {}", result.getAllErrors());
            return createBadRequestResponse("Validation failed: " + result.getAllErrors());
        }

        try {
            UserResponseDto registeredUser = userManagementService.registerUser(userDto);
            logger.info("Successfully registered new user with ID: {}", registeredUser.getId());
            return createSuccessResponse("User registered successfully", registeredUser);
        } catch (UserRegistrationException e) {
            logger.error("Failed to register user: {}", e.getMessage());
            return createBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during user registration", e);
            return createInternalServerErrorResponse("An unexpected error occurred during registration");
        }
    }

    /**
     * Updates a user's profile.
     *
     * @param userId  the ID of the user to update
     * @param userDto the updated user data
     * @return ResponseEntity containing the updated user profile
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody UserDto userDto) {
        logger.info("Attempting to update profile for user ID: {}", userId);

        User currentUser = getLoggedInUser();
        if (!currentUser.getId().equals(userId)) {
            logger.warn("Unauthorized attempt to update profile. Current user ID: {}, Target user ID: {}",
                    currentUser.getId(), userId);
            return createForbiddenResponse("You can only update your own profile");
        }

        try {
            UserResponseDto updatedUser = userManagementService.updateUserProfile(userId, userDto);
            logger.info("Successfully updated profile for user ID: {}", userId);
            return createSuccessResponse("User profile updated successfully", updatedUser);
        } catch (Exception e) {
            logger.error("Failed to update profile for user ID: {}", userId, e);
            return createInternalServerErrorResponse("Failed to update user profile");
        }
    }

    /**
     * Changes a user's password.
     *
     * @param userId      the ID of the user
     * @param oldPassword the current password
     * @param newPassword the new password
     * @return ResponseEntity with a success message
     */
    @PostMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        logger.info("Attempting to change password for user ID: {}", userId);

        User currentUser = getLoggedInUser();
        if (!currentUser.getId().equals(userId)) {
            logger.warn("Unauthorized attempt to change password. Current user ID: {}, Target user ID: {}",
                    currentUser.getId(), userId);
            return createForbiddenResponse("You can only change your own password");
        }

        try {
            userManagementService.changePassword(userId, oldPassword, newPassword);
            logger.info("Successfully changed password for user ID: {}", userId);
            return createSuccessResponse("Password changed successfully", null);
        } catch (Exception e) {
            logger.error("Failed to change password for user ID: {}", userId, e);
            return createInternalServerErrorResponse("Failed to change password");
        }
    }

    /**
     * Deletes a user account.
     *
     * @param userId the ID of the user to delete
     * @return ResponseEntity with a success message
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        logger.info("Attempting to delete user ID: {}", userId);

        User currentUser = getLoggedInUser();
        if (!currentUser.getId().equals(userId)) {
            logger.warn("Unauthorized attempt to delete account. Current user ID: {}, Target user ID: {}",
                    currentUser.getId(), userId);
            return createForbiddenResponse("You can only delete your own account");
        }

        try {
            userManagementService.deleteUser(userId);
            logger.info("Successfully deleted user ID: {}", userId);
            return createSuccessResponse("User deleted successfully", null);
        } catch (Exception e) {
            logger.error("Failed to delete user ID: {}", userId, e);
            return createInternalServerErrorResponse("Failed to delete user");
        }
    }
}