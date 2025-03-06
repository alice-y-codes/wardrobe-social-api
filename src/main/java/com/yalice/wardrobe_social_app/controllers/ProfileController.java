package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.profile.ProfileDto;
import com.yalice.wardrobe_social_app.dtos.profile.ProfileResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller responsible for handling profile-related operations.
 * Provides endpoints for managing user profiles.
 */
@RestController
@RequestMapping("/api/profiles")
public class ProfileController extends ApiBaseController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService, AuthUtils authUtils) {
        super(authUtils);
        this.profileService = profileService;
    }

    /**
     * Gets the current user's profile.
     *
     * @return ResponseEntity containing the user's profile
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponseDto>> getMyProfile() {
        return handleProfileRetrieval(() -> {
            User currentUser = getLoggedInUser();
            return profileService.getProfile(currentUser.getId());
        });
    }

    /**
     * Gets a user's profile by ID.
     *
     * @param userId the ID of the user
     * @return ResponseEntity containing the user's profile
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ProfileResponseDto>> getProfile(@PathVariable Long userId) {
        return handleProfileRetrieval(() -> profileService.getProfile(userId));
    }

    /**
     * Updates the current user's profile.
     *
     * @param profileDto the updated profile data
     * @param image      the new profile image file (optional)
     * @return ResponseEntity containing the updated profile
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponseDto>> updateProfile(
            @RequestPart("profile") ProfileDto profileDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return handleProfileUpdate(() -> {
            User currentUser = getLoggedInUser();
            return profileService.updateProfile(currentUser.getId(), profileDto, image);
        });
    }

    /**
     * Updates a user's profile visibility.
     *
     * @param isPublic whether the profile should be public
     * @return ResponseEntity containing the updated profile
     */
    @PutMapping("/me/visibility")
    public ResponseEntity<ApiResponse<ProfileResponseDto>> updateProfileVisibility(
            @RequestParam boolean isPublic) {
        return handleProfileUpdate(() -> {
            User currentUser = getLoggedInUser();
            return profileService.updateProfileVisibility(currentUser.getId(), isPublic);
        });
    }

    // Helper method for handling profile retrieval logic
    private ResponseEntity<ApiResponse<ProfileResponseDto>> handleProfileRetrieval(ProfileRetriever profileRetriever) {
        try {
            ProfileResponseDto profile = profileRetriever.execute();
            return createSuccessResponse("Profile retrieved successfully", profile);
        } catch (Exception e) {
            logger.error("Failed to retrieve profile", e);
            return createInternalServerErrorResponse("Failed to retrieve profile");
        }
    }

    // Helper method for handling profile update logic
    private ResponseEntity<ApiResponse<ProfileResponseDto>> handleProfileUpdate(ProfileUpdateAction profileUpdateAction) {
        try {
            ProfileResponseDto updatedProfile = profileUpdateAction.execute();
            return createSuccessResponse("Profile updated successfully", updatedProfile);
        } catch (Exception e) {
            logger.error("Failed to update profile", e);
            return createInternalServerErrorResponse("Failed to update profile");
        }
    }

    // Functional interfaces for generic handling
    @FunctionalInterface
    interface ProfileRetriever {
        ProfileResponseDto execute() throws Exception;
    }

    @FunctionalInterface
    interface ProfileUpdateAction {
        ProfileResponseDto execute() throws Exception;
    }
}
