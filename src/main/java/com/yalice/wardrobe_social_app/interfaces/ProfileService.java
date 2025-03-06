package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.dtos.profile.ProfileDto;
import com.yalice.wardrobe_social_app.dtos.profile.ProfileResponseDto;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.Profile.ProfileVisibility;
import com.yalice.wardrobe_social_app.entities.Wardrobe;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for managing user profiles.
 */
public interface ProfileService {
    /**
     * Gets a user's profile.
     *
     * @param userId the ID of the user
     * @return the user's profile
     */
    ProfileResponseDto getProfile(Long userId);

    /**
     * Updates a user's profile.
     *
     * @param userId     the ID of the user
     * @param profileDto the updated profile data
     * @param image      the new profile image file (optional)
     * @return the updated profile
     */
    ProfileResponseDto updateProfile(Long userId, ProfileDto profileDto, MultipartFile image);

    /**
     * Updates a user's profile visibility.
     *
     * @param userId   the ID of the user
     * @param isPublic whether the profile should be public
     * @return the updated profile
     */
    ProfileResponseDto updateProfileVisibility(Long userId, boolean isPublic);

    /**
     * Checks if a profile is accessible to a user.
     *
     * @param profileUserId the ID of the user whose profile is being accessed
     * @param viewerId      the ID of the user trying to access the profile
     * @return true if the profile is accessible, false otherwise
     */
    boolean isProfileAccessibleToUser(Long profileUserId, Long viewerId);

    Profile getProfileEntityById(Long profileId);

    Profile createProfile(Long userId, String bio, ProfileVisibility visibility);

    Wardrobe addWardrobeToProfile(Long profileId, String wardrobeName);

    void moveItemToAnotherWardrobe(Long itemId, Long newWardrobeId);
}
