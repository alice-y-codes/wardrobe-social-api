package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.ProfileUpdateRequest;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.FriendshipService;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;
    private final FriendshipService friendshipService;

    @Autowired
    public ProfileController(ProfileService profileService, UserService userService,
            FriendshipService friendshipService) {
        this.profileService = profileService;
        this.userService = userService;
        this.friendshipService = friendshipService;
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        Optional<User> currentUserOptional = getCurrentUser();
        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long currentUserId = currentUserOptional.get().getId();

        // Check if the profile is accessible to the current user
        if (!profileService.isProfileAccessibleToUser(userId, currentUserId)) {
            return ResponseEntity.status(403).body("You don't have permission to view this profile");
        }

        Optional<Profile> profileOptional = profileService.getProfileByUserId(userId);
        if (profileOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(profileOptional.get());
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId, @RequestBody ProfileUpdateRequest request) {
        Optional<User> currentUserOptional = getCurrentUser();
        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long currentUserId = currentUserOptional.get().getId();

        // Only the profile owner can update their profile
        if (!userId.equals(currentUserId)) {
            return ResponseEntity.status(403).body("You don't have permission to update this profile");
        }

        Profile updatedProfile = profileService.updateProfile(userId, request.getBio(), request.getVisibility());
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * Utility method to get the current authenticated user
     */
    private Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        String username = authentication.getName();
        return userService.findUserByUsername(username);
    }
}