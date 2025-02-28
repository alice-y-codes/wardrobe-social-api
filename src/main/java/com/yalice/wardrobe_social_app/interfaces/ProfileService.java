package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.Profile.ProfileVisibility;

import java.util.Optional;

public interface ProfileService {
    Optional<Profile> getProfileByUserId(Long userId);

    Profile createProfile(Long userId, String bio, ProfileVisibility visibility);

    Profile updateProfile(Long userId, String bio, ProfileVisibility visibility);

    boolean isProfileAccessibleToUser(Long profileOwnerId, Long viewerId);
}