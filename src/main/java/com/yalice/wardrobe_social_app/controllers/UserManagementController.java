package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.controllers.helpers.UserDtoValidator;
import com.yalice.wardrobe_social_app.dtos.user.UserDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.exceptions.UserRegistrationException;
import com.yalice.wardrobe_social_app.interfaces.UserManagementService;
import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling user management operations.
 * Provides endpoints for user registration, profile updates, and account management.
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
            return handleValidationErrors(result);
        }

        try {
            UserResponseDto registeredUser = userManagementService.registerUser(userDto);
            logger.info("Successfully registered new user with ID: {}", registeredUser.getId());
            return createSuccessResponse("User registered successfully", registeredUser);
        } catch (UserRegistrationException e) {
            return createBadRequestResponse(e.getMessage());
        } catch (Exception e) {
            return handleServiceError(e, "register user");
        }
    }

    /**
     * Updates a user's profile.
     *
     * @param userId  the ID of the user to update
     * @param userDto the updated user data
     * @return ResponseEntity containing the updated user profile
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserProfile(
            @PathVariable Long userId,
            @RequestBody UserDto userDto) {

        if (isUnauthorized(userId)) {
            return handleUnauthorizedAccess(userId, "update profile");
        }

        try {
            UserResponseDto updatedUser = userManagementService.updateUserProfile(userId, userDto);
            logger.info("Successfully updated profile for user ID: {}", userId);
            return createSuccessResponse("User profile updated successfully", updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            return handleServiceError(e, "update profile");
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

        if (isUnauthorized(userId)) {
            return handleUnauthorizedAccess(userId, "change password");
        }

        try {
            userManagementService.changePassword(userId, oldPassword, newPassword);
            logger.info("Successfully changed password for user ID: {}", userId);
            return createSuccessResponse("Password changed successfully", null);
        } catch (Exception e) {
            return handleServiceError(e, "change password");
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

        if (isUnauthorized(userId)) {
            return handleUnauthorizedAccess(userId, "delete account");
        }

        try {
            userManagementService.deleteUser(userId);
            logger.info("Successfully deleted user ID: {}", userId);
            return createSuccessResponse("User deleted successfully", null);
        } catch (Exception e) {
            return handleServiceError(e, "delete user");
        }
    }


}
