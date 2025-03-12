package com.yalice.wardrobe_social_app.services.core;

import com.yalice.wardrobe_social_app.dtos.profile.ProfileDto;
import com.yalice.wardrobe_social_app.dtos.profile.ProfileResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.entities.Wardrobe;
import com.yalice.wardrobe_social_app.interfaces.FriendService;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.mappers.ProfileMapper;
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.repositories.WardrobeRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import com.yalice.wardrobe_social_app.services.helpers.ImageHandlerService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProfileServiceImpl extends BaseService<Profile, Long> implements ProfileService {

    private final ProfileRepository profileRepository;
    private final WardrobeRepository wardrobeRepository;
    private final ItemRepository itemRepository;
    private final UserSearchService userSearchService;
    private final FriendService friendService;
    private final ProfileMapper profileMapper;
    private final ImageHandlerService imageHandler;

    public ProfileServiceImpl(
            ProfileRepository profileRepository,
            WardrobeRepository wardrobeRepository,
            ItemRepository itemRepository,
            UserSearchService userSearchService,
            FriendService friendService,
            ProfileMapper profileMapper,
            ImageHandlerService imageHandler) {
        this.profileRepository = profileRepository;
        this.wardrobeRepository = wardrobeRepository;
        this.itemRepository = itemRepository;
        this.userSearchService = userSearchService;
        this.friendService = friendService;
        this.profileMapper = profileMapper;
        this.imageHandler = imageHandler;
    }

    @Override
    protected JpaRepository<Profile, Long> getRepository() {
        return profileRepository;
    }

    @Override
    protected String getEntityName() {
        return "Profile";
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(Long userId) {
        logger.info("Retrieving profile for user ID: {}", userId);
        validationService.validateNotNull(userId, "User ID");
        return mapEntity(getProfileEntityByUserId(userId), profileMapper::toResponseDto);
    }

    @Override
    @Transactional
    public ProfileResponseDto updateProfile(Long userId, ProfileDto profileDto, MultipartFile image) {
        logger.info("Updating profile for user ID: {}", userId);

        validationService.validateNotNull(userId, "User ID");
        validationService.validateNotNull(profileDto, "Profile data");

        Profile profile = getProfileEntityByUserId(userId);
        updateProfileFields(profile, profileDto);
        profile.setProfileImageUrl(
                imageHandler.handleImageUpload(image, "profile", profile.getId(), profile.getProfileImageUrl()));

        return mapEntity(save(profile), profileMapper::toResponseDto);
    }

    @Override
    @Transactional
    public ProfileResponseDto updateProfileVisibility(Long userId, boolean isPublic) {
        logger.info("Updating profile visibility for user ID: {}", userId);

        validationService.validateNotNull(userId, "User ID");
        Profile profile = getProfileEntityByUserId(userId);
        profile.setVisibility(isPublic ? Profile.ProfileVisibility.PUBLIC : Profile.ProfileVisibility.PRIVATE);

        return mapEntity(save(profile), profileMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProfileAccessibleToUser(Long profileUserId, Long viewerId) {
        logger.debug("Checking profile accessibility for viewer ID: {} to profile ID: {}", viewerId, profileUserId);

        validationService.validateNotNull(profileUserId, "Profile user ID");
        validationService.validateNotNull(viewerId, "Viewer ID");

        if (profileUserId.equals(viewerId)) {
            return true;
        }

        Profile profile = getProfileEntityByUserId(profileUserId);
        return profile.getVisibility() == Profile.ProfileVisibility.PUBLIC ||
                friendService.areFriends(profileUserId, viewerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Profile getProfileEntityById(Long profileId) {
        validationService.validateNotNull(profileId, "Profile ID");
        return findById(profileId);
    }

    @Override
    @Transactional
    public Profile createProfile(Long userId, String bio, Profile.ProfileVisibility visibility) {
        logger.info("Creating profile for user ID: {}", userId);

        validationService.validateNotNull(userId, "User ID");
        validationService.validateNotNull(visibility, "Profile visibility");

        User user = userSearchService.getUserEntityById(userId);
        validationService.validateNotNull(user, "User");

        Profile profile = Profile.builder()
                .user(user)
                .bio(bio)
                .visibility(visibility)
                .build();

        profile = save(profile);

        // Create default wardrobe
        Wardrobe defaultWardrobe = Wardrobe.builder()
                .name("Wardrobe")
                .profile(profile)
                .build();
        wardrobeRepository.save(defaultWardrobe);

        return profile;
    }

    @Override
    @Transactional
    public Wardrobe addWardrobeToProfile(Long profileId, String wardrobeName) {
        logger.info("Adding wardrobe '{}' to profile ID: {}", wardrobeName, profileId);

        validationService.validateNotNull(profileId, "Profile ID");
        validationService.validateStringNotEmpty(wardrobeName, "Wardrobe name");

        Profile profile = findById(profileId);
        Wardrobe wardrobe = Wardrobe.builder()
                .name(wardrobeName)
                .profile(profile)
                .build();

        return wardrobeRepository.save(wardrobe);
    }

    @Override
    @Transactional
    public void moveItemToAnotherWardrobe(Long itemId, Long newWardrobeId) {
        logger.info("Moving item ID: {} to wardrobe ID: {}", itemId, newWardrobeId);

        validationService.validateNotNull(itemId, "Item ID");
        validationService.validateNotNull(newWardrobeId, "New wardrobe ID");

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));
        Wardrobe newWardrobe = wardrobeRepository.findById(newWardrobeId)
                .orElseThrow(() -> new IllegalArgumentException("Wardrobe not found with ID: " + newWardrobeId));

        validationService.validateOwnership(item.getProfile(), newWardrobe.getProfile().getId(), "item");

        item.setWardrobe(newWardrobe);
        itemRepository.save(item);
    }

    private Profile getProfileEntityByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with userId: " + userId));
    }

    private void updateProfileFields(Profile profile, ProfileDto profileDto) {
        profile.setBio(profileDto.getBio());
        profile.setLocation(profileDto.getLocation());
        profile.setStylePreferences(profileDto.getStylePreferences());
        profile.setFavoriteBrands(profileDto.getFavoriteBrands());
        profile.setFashionInspirations(profileDto.getFashionInspirations());
    }
}
