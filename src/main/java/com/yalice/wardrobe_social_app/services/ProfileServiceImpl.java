package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.profile.ProfileDto;
import com.yalice.wardrobe_social_app.dtos.profile.ProfileResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.entities.Wardrobe;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.FriendService;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.mappers.ProfileMapper;
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.repositories.WardrobeRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProfileServiceImpl extends BaseService implements ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);
    private final ProfileRepository profileRepository;
    private final WardrobeRepository wardrobeRepository;
    private final ItemRepository itemRepository;
    private final UserSearchService userSearchService;
    private final FriendService friendService;
    private final ProfileMapper profileMapper;

    public ProfileServiceImpl(ProfileRepository profileRepository, WardrobeRepository wardrobeRepository,
                              ItemRepository itemRepository, UserSearchService userSearchService,
                              FriendService friendService, ProfileMapper profileMapper) {
        this.profileRepository = profileRepository;
        this.wardrobeRepository = wardrobeRepository;
        this.itemRepository = itemRepository;
        this.userSearchService = userSearchService;
        this.friendService = friendService;
        this.profileMapper = profileMapper;
    }

    @Override
    public ProfileResponseDto getProfile(Long userId) {
        logger.info("Retrieving profile for user ID: {}", userId);
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with userId: " + userId));
        return profileMapper.toResponseDto(profile);
    }

    @Override
    @Transactional
    public ProfileResponseDto updateProfile(Long userId, ProfileDto profileDto, MultipartFile image) {
        logger.info("Updating profile for user ID: {}", userId);
        Profile profile = getProfileEntityByUserId(userId);

        profile.setBio(profileDto.getBio());
        profile.setLocation(profileDto.getLocation());
        profile.setStylePreferences(profileDto.getStylePreferences());
        profile.setFavoriteBrands(profileDto.getFavoriteBrands());
        profile.setFashionInspirations(profileDto.getFashionInspirations());

        if (image != null && !image.isEmpty()) {
            // TODO: Implement image upload logic
            profile.setProfileImageUrl("placeholder_url");
        }

        profileRepository.save(profile);
        logger.info("Profile updated successfully for user ID: {}", userId);
        return profileMapper.toResponseDto(profile);
    }

    @Override
    @Transactional
    public ProfileResponseDto updateProfileVisibility(Long userId, boolean isPublic) {
        logger.info("Updating profile visibility for user ID: {}", userId);
        Profile profile = getProfileEntityByUserId(userId);
        profile.setVisibility(isPublic ? Profile.ProfileVisibility.PUBLIC : Profile.ProfileVisibility.PRIVATE);
        profileRepository.save(profile);
        logger.info("Profile visibility updated successfully for user ID: {}", userId);
        return profileMapper.toResponseDto(profile);
    }

    @Override
    @Transactional
    public boolean isProfileAccessibleToUser(Long profileUserId, Long viewerId) {
        logger.debug("Checking profile accessibility for viewer ID: {} to profile ID: {}", viewerId, profileUserId);
        if (profileUserId.equals(viewerId)) return true;
        Profile profile = profileRepository.findByUserId(profileUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with userId: " + profileUserId));
        return profile.getVisibility() == Profile.ProfileVisibility.PUBLIC || friendService.areFriends(profileUserId, viewerId);
    }

    @Override
    public Profile getProfileEntityById(Long profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + profileId));
    }

    private Profile getProfileEntityByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with userId: " + userId));
    }

    @Override
    @Transactional
    public Profile createProfile(Long userId, String bio, Profile.ProfileVisibility visibility) {
        User user = userSearchService.getUserEntityById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        Profile profile = Profile.builder()
                .user(user)
                .bio(bio)
                .visibility(visibility)
                .build();

        profileRepository.save(profile);

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
        Profile profile = getProfileEntityById(profileId);
        Wardrobe wardrobe = Wardrobe.builder()
                .name(wardrobeName)
                .profile(profile)
                .build();
        return wardrobeRepository.save(wardrobe);
    }

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
}
