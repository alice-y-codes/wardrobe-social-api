package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.controllers.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.dtos.user.ChangePasswordDto;
import com.yalice.wardrobe_social_app.dtos.user.UserRegistrationDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.exceptions.UsernameAlreadyExistsException;
import com.yalice.wardrobe_social_app.interfaces.UserManagementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling user management operations.
 */
@RestController
@RequestMapping("/api/users")
public class UserManagementController extends ApiBaseController {

    private final UserManagementService userManagementService;

    @Autowired
    public UserManagementController(UserManagementService userManagementService, AuthUtils authUtils) {
        super(authUtils);
        this.userManagementService = userManagementService;
    }

    /**
     * Registers a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> registerUser(
            @Valid @RequestBody UserRegistrationDto registrationDto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }

        return handleEntityAction(() -> {
            if (userManagementService.existsByUsername(registrationDto.getUsername())) {
                throw new UsernameAlreadyExistsException("Username already exists: " + registrationDto.getUsername());
            }
            return userManagementService.registerUser(registrationDto);
        }, "register", "User", "registered");
    }
    /**
     * Changes a user's password.
     */
    @PutMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordDto passwordDto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }

        return handleVoidAction(() -> {
            // First check if the user exists
            if (!userManagementService.existsById(userId)) {
                throw new ResourceNotFoundException("User not found");
            }

            // Then check authorization
            User currentUser = getLoggedInUser();
            if (!currentUser.getId().equals(userId)) {
                throw new SecurityException("Unauthorized");
            }

            userManagementService.changePassword(userId, passwordDto);
        }, "change", "Password", "changed");
    }

    /**
     * Deletes a user account.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
        return handleVoidAction(() -> {
            // First check if the user exists
            if (!userManagementService.existsById(userId)) {
                throw new ResourceNotFoundException("User not found");
            }

            // Then check authorization
            User currentUser = getLoggedInUser();
            if (!currentUser.getId().equals(userId)) {
                throw new SecurityException("Unauthorized");
            }

            userManagementService.deleteUser(userId);
        }, "delete", "User", "deleted");
    }
}
