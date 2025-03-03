package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.UserRegistrationException;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller responsible for handling user-related operations.
 * Provides endpoints for user registration, retrieval, and management.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /** Service for user-related operations. */
    private final UserService userService;

    /**
     * Constructor for UserController.
     *
     * @param userService Service for user-related operations
     */
    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user in the system.
     *
     * @param user User object containing registration details
     * @return ResponseEntity with the registered user or error message
     * @throws UserRegistrationException If registration validation fails
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody final User user) {
        validateUser(user);

        final Optional<User> registeredUser = userService.registerUser(user);

        if (registeredUser.isEmpty()) {
            throw new UserRegistrationException("Username already taken");
        }

        return ResponseEntity.ok(registeredUser.get());
    }

    /**
     * Finds a user by their username.
     *
     * @param username Username to search for
     * @return ResponseEntity with the found user or 404 if not found
     */
    @GetMapping("/findByUsername")
    public ResponseEntity<User> findUserByUsername(@RequestParam final String username) {
        final Optional<User> foundUser = userService.findUserByUsername(username);
        return foundUser.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // TODO: Implement user management endpoints
    // @GetMapping("/me")
    // public ResponseEntity<?> getCurrentUser() {
    // // Get current authenticated user
    // }
    //
    // @DeleteMapping("/{userId}")
    // public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
    // // Delete logic
    // }
    //
    // @PutMapping("/update")
    // public ResponseEntity<?> updateUser(@RequestBody User user) {
    // // Validate, then update
    // }
    //
    // @PostMapping("/password/change")
    // public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest
    // request) {
    // // Change password logic
    // }

    /**
     * Validates user registration data.
     * Checks for required fields and password strength.
     *
     * @param user User object to validate
     * @throws UserRegistrationException If validation fails
     */
    private static void validateUser(final User user) {
        // Validate the username
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new UserRegistrationException("Username is required");
        }

        // Validate the password
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new UserRegistrationException("Password is required");
        }
        if (user.getPassword().length() < 8) {
            throw new UserRegistrationException(
                    "Password must be at least 8 characters");
        }

        // Validate the email
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new UserRegistrationException("Email is required");
        }

        // Validate the provider
        if (user.getProvider() == null) {
            throw new UserRegistrationException("Provider is required");
        }
    }
}
