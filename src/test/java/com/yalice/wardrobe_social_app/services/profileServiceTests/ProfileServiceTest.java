package com.yalice.wardrobe_social_app.services.profileServiceTests;

import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.Profile.ProfileVisibility;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.FriendshipService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.services.ProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserService userService;

    @Mock
    private FriendshipService friendshipService;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private User testUser;
    private Profile testProfile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        testProfile = Profile.builder()
                .id(1L)
                .user(testUser)
                .bio("Test bio")
                .visibility(ProfileVisibility.PUBLIC)
                .build();
    }

    @Test
    void getProfileByUserId_whenProfileExists_returnsProfile() {
        // Arrange
        when(profileRepository.findByUserId(anyLong())).thenReturn(Optional.of(testProfile));

        // Act
        Optional<Profile> result = profileService.getProfileByUserId(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testProfile, result.get());
        verify(profileRepository).findByUserId(1L);
    }

    @Test
    void getProfileByUserId_whenProfileDoesNotExist_returnsEmpty() {
        // Arrange
        when(profileRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<Profile> result = profileService.getProfileByUserId(1L);

        // Assert
        assertFalse(result.isPresent());
        verify(profileRepository).findByUserId(1L);
    }

    @Test
    void createProfile_createsAndReturnsNewProfile() {
        // Arrange
        when(userService.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(profileRepository.save(any(Profile.class))).thenReturn(testProfile);

        // Act
        Profile result = profileService.createProfile(1L, "Test bio", ProfileVisibility.PUBLIC);

        // Assert
        assertNotNull(result);
        assertEquals("Test bio", result.getBio());
        assertEquals(ProfileVisibility.PUBLIC, result.getVisibility());
        verify(userService).findById(1L);
        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void updateProfile_whenProfileExists_updatesAndReturnsProfile() {
        // Arrange
        when(profileRepository.findByUserId(anyLong())).thenReturn(Optional.of(testProfile));
        when(profileRepository.save(any(Profile.class))).thenReturn(testProfile);

        // Act
        Profile result = profileService.updateProfile(1L, "Updated bio", ProfileVisibility.FRIENDS_ONLY);

        // Assert
        assertNotNull(result);
        assertEquals("Updated bio", result.getBio());
        assertEquals(ProfileVisibility.FRIENDS_ONLY, result.getVisibility());
        verify(profileRepository).findByUserId(1L);
        verify(profileRepository).save(testProfile);
    }

    @Test
    void isProfileAccessibleToUser_whenSameUser_returnsTrue() {
        // Act
        boolean result = profileService.isProfileAccessibleToUser(1L, 1L);

        // Assert
        assertTrue(result);
    }

    @Test
    void isProfileAccessibleToUser_whenProfilePublic_returnsTrue() {
        // Arrange
        when(profileRepository.findByUserId(anyLong())).thenReturn(Optional.of(testProfile));

        // Act
        boolean result = profileService.isProfileAccessibleToUser(1L, 2L);

        // Assert
        assertTrue(result);
        verify(profileRepository).findByUserId(1L);
    }

    @Test
    void isProfileAccessibleToUser_whenProfilePrivateAndNotFriends_returnsFalse() {
        // Arrange
        testProfile.setVisibility(ProfileVisibility.PRIVATE);
        when(profileRepository.findByUserId(anyLong())).thenReturn(Optional.of(testProfile));
        when(friendshipService.areFriends(anyLong(), anyLong())).thenReturn(false);

        // Act
        boolean result = profileService.isProfileAccessibleToUser(1L, 2L);

        // Assert
        assertFalse(result);
        verify(profileRepository).findByUserId(1L);
        verify(friendshipService).areFriends(1L, 2L);
    }

    @Test
    void isProfileAccessibleToUser_whenProfileFriendsOnlyAndAreFriends_returnsTrue() {
        // Arrange
        testProfile.setVisibility(ProfileVisibility.FRIENDS_ONLY);
        when(profileRepository.findByUserId(anyLong())).thenReturn(Optional.of(testProfile));
        when(friendshipService.areFriends(anyLong(), anyLong())).thenReturn(true);

        // Act
        boolean result = profileService.isProfileAccessibleToUser(1L, 2L);

        // Assert
        assertTrue(result);
        verify(profileRepository).findByUserId(1L);
        verify(friendshipService).areFriends(1L, 2L);
    }
}