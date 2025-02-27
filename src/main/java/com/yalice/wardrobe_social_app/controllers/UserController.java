package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.UserRegistrationException;
import com.yalice.wardrobe_social_app.interfaces.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        validateRequest(user);

        Optional<User> registeredUser = userService.registerUser(user);

        if (registeredUser.isEmpty()) {
            throw new UserRegistrationException("Username already taken");
        }

        return ResponseEntity.ok(registeredUser.get());
    }

    private void validateRequest(User user) {
        if (user == null) {
            throw new UserRegistrationException("User data cannot be null");
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new UserRegistrationException("Username cannot be null or empty");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new UserRegistrationException("Email cannot be null or empty");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new UserRegistrationException("Password cannot be null or empty");
        }
        if (user.getProvider() == null || user.getProvider().trim().isEmpty()) {
            throw new UserRegistrationException("Provider cannot be null or empty");
        }
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
}
