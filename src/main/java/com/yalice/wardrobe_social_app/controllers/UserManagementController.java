package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.controllers.helpers.UserDtoValidator;
import com.yalice.wardrobe_social_app.dtos.user.UserDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.interfaces.UserManagementService;
import com.yalice.wardrobe_social_app.controllers.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling user management operations.
 */
@RestController
@RequestMapping("/users")
public class UserManagementController extends ApiBaseController {

    private final UserManagementService userManagementService;
    private final UserDtoValidator userDtoValidator;

    @Autowired
    public UserManagementController(UserManagementService userManagementService, AuthUtils authUtils, UserDtoValidator userDtoValidator) {
        super(authUtils);
        this.userManagementService = userManagementService;
        this.userDtoValidator = userDtoValidator;
    }

    /**
     * Registers a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> registerUser(@RequestBody UserDto userDto, BindingResult result) {
        logger.info("Attempting to register new user: {}", userDto.getUsername());

        userDtoValidator.validate(userDto, result);
        if (result.hasErrors()) {
            return handleValidationErrors(result);
        }

        return handleEntityCreation(() -> userManagementService.registerUser(userDto), "User Registration");
    }

    /**
     * Updates a user's profile.
     */
    @PatchMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUserProfile(@PathVariable Long userId, @RequestBody UserDto userDto) {
        if (isUnauthorized(userId)) {
            return handleUnauthorizedAccess(userId, "update profile");
        }
        return handleEntityUpdate(() -> userManagementService.updateUserProfile(userId, userDto), "User Profile");
    }

    /**
     * Changes a user's password.
     */
    @PostMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@PathVariable Long userId,
                                                            @RequestParam String oldPassword,
                                                            @RequestParam String newPassword) {
        if (isUnauthorized(userId)) {
            return handleUnauthorizedAccess(userId, "change password");
        }
        return handleVoidAction(() -> userManagementService.changePassword(userId, oldPassword, newPassword), "Password Change", "User Password");
    }

    /**
     * Deletes a user account.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        if (isUnauthorized(userId)) {
            return handleUnauthorizedAccess(userId, "delete account");
        }
        return handleEntityDeletion(() -> userManagementService.deleteUser(userId), "User Account");
    }
}
