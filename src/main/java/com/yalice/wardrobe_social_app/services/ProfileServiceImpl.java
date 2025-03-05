package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.Profile.ProfileVisibility;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.entities.Wardrobe;
import com.yalice.wardrobe_social_app.interfaces.FriendshipService;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.repositories.WardrobeRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProfileServiceImpl extends BaseService implements ProfileService {

    private final ProfileRepository profileRepository;
    private final WardrobeRepository wardrobeRepository;
    private final ItemRepository itemRepository;
    private final UserSearchService userSearchService;
    private final FriendshipService friendshipService;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository, WardrobeRepository wardrobeRepository, ItemRepository itemRepository,
                              UserSearchService userSearchService, FriendshipService friendshipService) {
        this.profileRepository = profileRepository;
        this.wardrobeRepository = wardrobeRepository;
        this.itemRepository = itemRepository;
        this.userSearchService = userSearchService;
        this.friendshipService = friendshipService;
    }

    @Override
    public Optional<Profile> getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Profile createProfile(Long userId, String bio, ProfileVisibility visibility) {
        User user = userSearchService.getUserEntityById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        Profile profile = Profile.builder()
                .user(user)
                .bio(bio)
                .visibility(visibility)
                .build();

        // Save profile to the database
        profile = profileRepository.save(profile);

        // Create and save default wardrobe
        Wardrobe defaultWardrobe = Wardrobe.builder()
                .name("Wardrobe")
                .profile(profile)
                .build();
        wardrobeRepository.save(defaultWardrobe);

        return profile;
    }

    @Override
    @Transactional
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
    @Transactional
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

    /**
     * Add a new wardrobe for the given profile.
     */
    @Override
    @Transactional
    public Wardrobe addWardrobeToProfile(Long profileId, String wardrobeName) {
        Optional<Profile> profileOptional = profileRepository.findById(profileId);
        if (profileOptional.isEmpty()) {
            throw new IllegalArgumentException("Profile not found for ID: " + profileId);
        }

        Profile profile = profileOptional.get();
        Wardrobe wardrobe = Wardrobe.builder()
                .name(wardrobeName)
                .profile(profile)
                .build();

        return wardrobeRepository.save(wardrobe);
    }

    /**
     * Move an item to a different wardrobe within the same profile.
     */
    @Override
    @Transactional
    public void moveItemToAnotherWardrobe(Long itemId, Long newWardrobeId) {
        // Fetch the item to move
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));

        // Find the new wardrobe
        Wardrobe newWardrobe = wardrobeRepository.findById(newWardrobeId)
                .orElseThrow(() -> new IllegalArgumentException("Wardrobe not found with ID: " + newWardrobeId));

        // Update the item's wardrobe
        item.setWardrobe(newWardrobe);
        itemRepository.save(item);
    }
}
