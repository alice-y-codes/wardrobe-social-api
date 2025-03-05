package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.Profile.ProfileVisibility;
import com.yalice.wardrobe_social_app.entities.Wardrobe;

import java.util.Optional;

public interface ProfileService {
    Optional<Profile> getProfileByUserId(Long userId);

    Profile createProfile(Long userId, String bio, ProfileVisibility visibility);

    Profile updateProfile(Long userId, String bio, ProfileVisibility visibility);

    boolean isProfileAccessibleToUser(Long profileOwnerId, Long viewerId);

    /**
     * Add a new wardrobe for the given profile.
     */
    Wardrobe addWardrobeToProfile(Long profileId, String wardrobeName);

    /**
     * Move an item to a different wardrobe within the same profile.
     */
    void moveItemToAnotherWardrobe(Long itemId, Long newWardrobeId);
}
