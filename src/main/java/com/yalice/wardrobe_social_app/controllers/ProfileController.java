package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.profile.ProfileDto;
import com.yalice.wardrobe_social_app.dtos.profile.ProfileResponseDto;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller responsible for handling profile-related operations.
 */
@RestController
@RequestMapping("/profiles")
public class ProfileController extends ApiBaseController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService, AuthUtils authUtils) {
        super(authUtils);
        this.profileService = profileService;
    }

    /**
     * Gets the current user's profile.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponseDto>> getMyProfile() {
        return handleEntityRetrieval(() -> profileService.getProfile(getLoggedInUser().getId()),
                "Profile retrieved successfully");
    }

    /**
     * Gets a user's profile by ID.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ProfileResponseDto>> getProfile(@PathVariable Long userId) {
        return handleEntityRetrieval(() -> profileService.getProfile(userId),
                "Profile retrieved successfully");
    }

    /**
     * Updates the current user's profile.
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ProfileResponseDto>> updateProfile(
            @RequestPart("profile") ProfileDto profileDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return handleEntityUpdate(() -> profileService.updateProfile(getLoggedInUser().getId(), profileDto, image),
                "Profile updated successfully");
    }

    /**
     * Updates the current user's profile visibility.
     */
    @PutMapping("/me/visibility")
    public ResponseEntity<ApiResponse<ProfileResponseDto>> updateProfileVisibility(@RequestParam boolean isPublic) {
        return handleEntityUpdate(() -> profileService.updateProfileVisibility(getLoggedInUser().getId(), isPublic),
                "Profile visibility updated successfully");
    }
}
