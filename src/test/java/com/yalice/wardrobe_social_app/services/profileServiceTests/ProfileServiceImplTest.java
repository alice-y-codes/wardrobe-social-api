package com.yalice.wardrobe_social_app.services.profileServiceTests;

import com.yalice.wardrobe_social_app.dtos.profile.ProfileDto;
import com.yalice.wardrobe_social_app.dtos.profile.ProfileResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.entities.Wardrobe;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.FriendService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.mappers.ProfileMapper;
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.repositories.WardrobeRepository;
import com.yalice.wardrobe_social_app.services.ProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceImplTest {

    @Mock private ProfileRepository profileRepository;
    @Mock private WardrobeRepository wardrobeRepository;
    @Mock private ItemRepository itemRepository;
    @Mock private UserSearchService userSearchService;
    @Mock private FriendService friendService;
    @Mock private ProfileMapper profileMapper;

    @InjectMocks private ProfileServiceImpl profileService;

    private Long userId;
    private Long profileId;
    private Long viewerId;
    private ProfileDto profileDto;
    private MultipartFile image;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 1L;
        profileId = 1L;
        viewerId = 1L;
        profileDto = new ProfileDto("New Bio", false, "New Location", "New Style", "New Brands", "New Inspirations");
        image = mock(MultipartFile.class);
    }

    @Test
    void shouldReturnProfileWhenExists() {
        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setUser(new User());
        profile.getUser().setId(userId);

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(profileMapper.toResponseDto(profile)).thenReturn(
                ProfileResponseDto.builder()
                        .id(profileId)
                        .userId(userId)
                        .bio("New Bio")
                        .location("New Location")
                        .isPublic(true)
                        .build()
        );

        ProfileResponseDto result = profileService.getProfile(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(profileMapper).toResponseDto(profile);
    }

    @Test
    void shouldThrowExceptionWhenProfileNotFound() {
        when(profileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> profileService.getProfile(userId));

        assertEquals("Profile not found with userId: " + userId, exception.getMessage());
    }

    @Test
    void shouldUpdateProfile() {
        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setUser(new User());
        profile.getUser().setId(userId);
        profile.setBio("Old Bio");

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(profileRepository.save(profile)).thenReturn(profile);
        when(profileMapper.toResponseDto(profile)).thenReturn(
                ProfileResponseDto.builder()
                        .id(profileId)
                        .userId(userId)
                        .bio("New Bio")
                        .location("New Location")
                        .isPublic(true)
                        .build()
        );

        ProfileResponseDto updatedProfile = profileService.updateProfile(userId, profileDto, image);

        assertNotNull(updatedProfile);
        assertEquals("New Bio", updatedProfile.getBio());
        verify(profileRepository).save(profile);
        verify(profileMapper).toResponseDto(profile);
    }

    @Test
    void shouldUpdateProfileVisibility() {
        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setUser(new User());
        profile.getUser().setId(userId);
        profile.setVisibility(Profile.ProfileVisibility.PRIVATE);

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(profileRepository.save(profile)).thenReturn(profile);
        when(profileMapper.toResponseDto(profile)).thenReturn(
                ProfileResponseDto.builder()
                        .id(profileId)
                        .userId(userId)
                        .bio("New Bio")
                        .location("New Location")
                        .isPublic(true)
                        .build()
        );

        ProfileResponseDto updatedProfile = profileService.updateProfileVisibility(userId, true);

        assertNotNull(updatedProfile);
        assertTrue(updatedProfile.isPublic());
        verify(profileMapper).toResponseDto(profile);
    }

    @Test
    void shouldDetermineProfileAccessibility() {
        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setUser(new User());
        profile.getUser().setId(userId);
        profile.setVisibility(Profile.ProfileVisibility.PUBLIC);

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(friendService.areFriends(userId, viewerId)).thenReturn(true);

        boolean isAccessible = profileService.isProfileAccessibleToUser(profileId, viewerId);

        assertTrue(isAccessible);
    }

    @Test
    void shouldCreateProfile() {
        User user = new User();
        user.setId(userId);
        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setUser(user);

        when(userSearchService.getUserEntityById(userId)).thenReturn(user);
        when(profileRepository.save(profile)).thenReturn(profile);

        Profile createdProfile = profileService.createProfile(userId, "Test Bio", Profile.ProfileVisibility.PUBLIC);

        assertNotNull(createdProfile);
        assertEquals(userId, createdProfile.getUser().getId());
    }

    @Test
    void shouldThrowExceptionWhenCreatingProfileForNonexistentUser() {
        when(userSearchService.getUserEntityById(userId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> profileService.createProfile(userId, "Test Bio", Profile.ProfileVisibility.PUBLIC));

        assertEquals("User not found with ID: " + userId, exception.getMessage());
    }

    @Test
    void shouldMoveItemToAnotherWardrobe() {
        Item item = new Item();
        item.setId(1L);
        Wardrobe newWardrobe = new Wardrobe();
        newWardrobe.setId(2L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(wardrobeRepository.findById(2L)).thenReturn(Optional.of(newWardrobe));

        profileService.moveItemToAnotherWardrobe(1L, 2L);

        assertEquals(newWardrobe, item.getWardrobe());
    }
}
