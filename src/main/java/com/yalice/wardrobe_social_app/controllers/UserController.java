package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.UserRegistrationException;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        validateUser(user);

        Optional<User> registeredUser = userService.registerUser(user);

        if (registeredUser.isEmpty()) {
            throw new UserRegistrationException("Username already taken");
        }

        return ResponseEntity.ok(registeredUser.get());
    }

    @GetMapping("/findByUsername")
    public ResponseEntity<User> findUserByUsername(@RequestParam String username) {
        Optional<User> foundUser = userService.findUserByUsername(username);
        return foundUser.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // @GetMapping("/me")
    // public ResponseEntity<?> getCurrentUser() {
    // // Get current authenticated user
    // }

    // @DeleteMapping("/{userId}")
    // public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
    // // Delete logic
    // }

    // @PutMapping("/update")
    // public ResponseEntity<?> updateUser(@RequestBody User user) {
    // // Validate, then update
    // }

    // @PostMapping("/password/change")
    // public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest
    // request) {
    // // Change password logic
    // }

    private static void validateUser(User user) {
        // Validate the username
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new UserRegistrationException("Username is required");
        }

        // Validate the password
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new UserRegistrationException("Password is required");
        }
        if (user.getPassword().length() < 8) {
            throw new UserRegistrationException("Password must be at least 8 characters");
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
