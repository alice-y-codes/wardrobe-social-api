package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.profile.ProfileDto;
import com.yalice.wardrobe_social_app.dtos.profile.ProfileResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.Profile.ProfileVisibility;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.entities.Wardrobe;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class ProfileServiceImpl extends BaseService implements ProfileService {

    private final ProfileRepository profileRepository;
    private final WardrobeRepository wardrobeRepository;
    private final ItemRepository itemRepository;
    private final UserSearchService userSearchService;
    private final FriendshipService friendshipService;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository, WardrobeRepository wardrobeRepository,
            ItemRepository itemRepository,
            UserSearchService userSearchService, FriendshipService friendshipService) {
        this.profileRepository = profileRepository;
        this.wardrobeRepository = wardrobeRepository;
        this.itemRepository = itemRepository;
        this.userSearchService = userSearchService;
        this.friendshipService = friendshipService;
    }

    @Override
    public ProfileResponseDto getProfile(Long userId) {
        logger.info("Retrieving profile for user ID: {}", userId);

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with userId: " + userId));

        return convertToProfileResponseDto(profile);
    }

    @Override
    @Transactional
    public ProfileResponseDto updateProfile(Long userId, ProfileDto profileDto, MultipartFile image) {
        logger.info("Attempting to update profile for user ID: {}", userId);

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with userId: " + userId));

        profile.setBio(profileDto.getBio());
        profile.setLocation(profileDto.getLocation());
        profile.setStylePreferences(profileDto.getStylePreferences());
        profile.setFavoriteBrands(profileDto.getFavoriteBrands());
        profile.setFashionInspirations(profileDto.getFashionInspirations());

        if (image != null && !image.isEmpty()) {
            // TODO: Implement image upload logic
            profile.setProfileImageUrl("placeholder_url");
        }

        Profile updatedProfile = profileRepository.save(profile);
        logger.info("Profile updated successfully for user ID: {}", userId);

        return convertToProfileResponseDto(updatedProfile);
    }

    @Override
    @Transactional
    public ProfileResponseDto updateProfileVisibility(Long userId, boolean isPublic) {
        logger.info("Attempting to update profile visibility for user ID: {} to: {}", userId, isPublic);

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with userId: " + userId));

        profile.setVisibility(isPublic ? ProfileVisibility.PUBLIC : ProfileVisibility.PRIVATE);
        Profile updatedProfile = profileRepository.save(profile);
        logger.info("Profile visibility updated successfully for user ID: {}", userId);

        return convertToProfileResponseDto(updatedProfile);
    }

    @Override
    @Transactional
    public boolean isProfileAccessibleToUser(Long profileUserId, Long viewerId) {
        logger.info("Checking profile accessibility for user ID: {} to view profile of user ID: {}", viewerId,
                profileUserId);

        // Users can always view their own profiles
        if (profileUserId.equals(viewerId)) {
            return true;
        }

        Optional<Profile> profileOptional = profileRepository.findByUserId(profileUserId);
        if (profileOptional.isEmpty()) {
            return false;
        }

        Profile profile = profileOptional.get();
        ProfileVisibility visibility = profile.getVisibility();

        // Public profiles are accessible to everyone
        if (visibility == ProfileVisibility.PUBLIC) {
            return true;
        }

        // Private profiles are only accessible to friends
        return friendshipService.areFriends(profileUserId, viewerId);
    }

    @Override
    public Profile getProfileEntityById(Long profileId) {
        return profileRepository.findById(profileId).orElseThrow(
                () -> new ResourceNotFoundException("Profile not found with id: " + profileId));
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

        profile = profileRepository.save(profile);

        Wardrobe defaultWardrobe = Wardrobe.builder()
                .name("Wardrobe")
                .profile(profile)
                .build();
        wardrobeRepository.save(defaultWardrobe);

        return profile;
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
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));

        Wardrobe newWardrobe = wardrobeRepository.findById(newWardrobeId)
                .orElseThrow(() -> new IllegalArgumentException("Wardrobe not found with ID: " + newWardrobeId));

        item.setWardrobe(newWardrobe);
        itemRepository.save(item);
    }

    private ProfileResponseDto convertToProfileResponseDto(Profile profile) {
        return ProfileResponseDto.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .username(profile.getUser().getUsername())
                .bio(profile.getBio())
                .location(profile.getLocation())
                .stylePreferences(profile.getStylePreferences())
                .favoriteBrands(profile.getFavoriteBrands())
                .fashionInspirations(profile.getFashionInspirations())
                .profileImageUrl(profile.getProfileImageUrl())
                .isPublic(profile.getVisibility() == ProfileVisibility.PUBLIC)
                .build();
    }
}
