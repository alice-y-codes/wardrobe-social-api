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
        logger.info("Retrieving profile for current user");

        User currentUser = getLoggedInUser();
        try {
            ProfileResponseDto profile = profileService.getProfile(currentUser.getId());
            logger.info("Successfully retrieved profile for user ID: {}", currentUser.getId());
            return createSuccessResponse("Profile retrieved successfully", profile);
        } catch (Exception e) {
            logger.error("Failed to retrieve profile for user ID: {}", currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to retrieve profile");
        }
    }

    /**
     * Gets a user's profile by ID.
     *
     * @param userId the ID of the user
     * @return ResponseEntity containing the user's profile
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ProfileResponseDto>> getProfile(@PathVariable Long userId) {
        logger.info("Retrieving profile for user ID: {}", userId);

        try {
            ProfileResponseDto profile = profileService.getProfile(userId);
            logger.info("Successfully retrieved profile for user ID: {}", userId);
            return createSuccessResponse("Profile retrieved successfully", profile);
        } catch (Exception e) {
            logger.error("Failed to retrieve profile for user ID: {}", userId, e);
            return createNotFoundResponse("Profile not found for user ID: " + userId);
        }
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
        logger.info("Attempting to update profile for current user");

        User currentUser = getLoggedInUser();
        try {
            ProfileResponseDto updatedProfile = profileService.updateProfile(currentUser.getId(), profileDto, image);
            logger.info("Successfully updated profile for user ID: {}", currentUser.getId());
            return createSuccessResponse("Profile updated successfully", updatedProfile);
        } catch (Exception e) {
            logger.error("Failed to update profile for user ID: {}", currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to update profile");
        }
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
        logger.info("Attempting to update profile visibility for current user to: {}", isPublic);

        User currentUser = getLoggedInUser();
        try {
            ProfileResponseDto updatedProfile = profileService.updateProfileVisibility(currentUser.getId(), isPublic);
            logger.info("Successfully updated profile visibility for user ID: {} to: {}",
                    currentUser.getId(), isPublic);
            return createSuccessResponse("Profile visibility updated successfully", updatedProfile);
        } catch (Exception e) {
            logger.error("Failed to update profile visibility for user ID: {}", currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to update profile visibility");
        }
    }
}