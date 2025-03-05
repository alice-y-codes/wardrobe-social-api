package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.Profile.ProfileVisibility;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.FriendshipService;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserSearchService userSearchService;
    private final FriendshipService friendshipService;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository, UserSearchService userSearchService,
            FriendshipService friendshipService) {
        this.profileRepository = profileRepository;
        this.userSearchService = userSearchService;
        this.friendshipService = friendshipService;
    }

    @Override
    public Optional<Profile> getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId);
    }

    @Override
    public Profile createProfile(Long userId, String bio, ProfileVisibility visibility) {
        Optional<User> userOptional = userSearchService.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        User user = userOptional.get();
        Profile profile = Profile.builder()
                .user(user)
                .bio(bio)
                .visibility(visibility)
                .build();

        return profileRepository.save(profile);
    }

    @Override
    public Profile updateProfile(Long userId, String bio, ProfileVisibility visibility) {
        Optional<Profile> profileOptional = profileRepository.findByUserId(userId);
        if (profileOptional.isEmpty()) {
            throw new IllegalArgumentException("Profile not found for user with ID: " + userId);
        }

        Profile profile = profileOptional.get();
        profile.setBio(bio);
        profile.setVisibility(visibility);

        return profileRepository.save(profile);
    }

    @Override
    public boolean isProfileAccessibleToUser(Long profileOwnerId, Long viewerId) {
        // Users can always view their own profiles
        if (profileOwnerId.equals(viewerId)) {
            return true;
        }

        Optional<Profile> profileOptional = profileRepository.findByUserId(profileOwnerId);
        if (profileOptional.isEmpty()) {
            return false;
        }

        Profile profile = profileOptional.get();
        ProfileVisibility visibility = profile.getVisibility();

        // Public profiles are accessible to everyone
        if (visibility == ProfileVisibility.PUBLIC) {
            return true;
        }

        // Private profiles are only accessible to the owner (handled above)
        if (visibility == ProfileVisibility.PRIVATE) {

            return friendshipService.areFriends(profileOwnerId, viewerId);
        }

        // FRIENDS_ONLY profiles are accessible to friends
        return friendshipService.areFriends(profileOwnerId, viewerId);
    }
}