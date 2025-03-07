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
import com.yalice.wardrobe_social_app.repositories.ItemRepository;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.repositories.WardrobeRepository;
import com.yalice.wardrobe_social_app.services.ProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private WardrobeRepository wardrobeRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserSearchService userSearchService;

    @Mock
    private FriendService friendService;

    @InjectMocks
    private ProfileServiceImpl profileService;

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
        profileDto = new ProfileDto("New Bio", false,  "New Location", "New Style", "New Brands", "New Inspirations");
        image = mock(MultipartFile.class);
    }

    @Test
    void testGetProfile() {
        // Mock Profile data
        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setUser(new User());
        profile.getUser().setId(userId);

        when(profileRepository.findByUserId(userId)).thenReturn(java.util.Optional.of(profile));

        ProfileResponseDto profileResponse = profileService.getProfile(userId);

        assertNotNull(profileResponse);
        assertEquals(userId, profileResponse.getUserId());
    }

    @Test
    void testGetProfileNotFound() {
        when(profileRepository.findByUserId(userId)).thenReturn(java.util.Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> profileService.getProfile(userId));

        assertEquals("Profile not found with userId: " + userId, exception.getMessage());
    }

    @Test
    void testUpdateProfile() {
        // Mock existing profile
        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setUser(new User());
        profile.getUser().setId(userId);
        profile.setBio("Old Bio");

        when(profileRepository.findByUserId(userId)).thenReturn(java.util.Optional.of(profile));
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        ProfileResponseDto updatedProfile = profileService.updateProfile(userId, profileDto, image);

        assertNotNull(updatedProfile);
        assertEquals("New Bio", updatedProfile.getBio());
    }

    @Test
    void testUpdateProfileNotFound() {
        when(profileRepository.findByUserId(userId)).thenReturn(java.util.Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> profileService.updateProfile(userId, profileDto, image));

        assertEquals("Profile not found with userId: " + userId, exception.getMessage());
    }

    @Test
    void testUpdateProfileVisibility() {
        // Mock profile data
        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setUser(new User());
        profile.getUser().setId(userId);
        profile.setVisibility(Profile.ProfileVisibility.PRIVATE);

        when(profileRepository.findByUserId(userId)).thenReturn(java.util.Optional.of(profile));
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        ProfileResponseDto updatedProfile = profileService.updateProfileVisibility(userId, true);

        assertNotNull(updatedProfile);
        assertTrue(updatedProfile.isPublic());
    }

    @Test
    void testIsProfileAccessibleToUser() {
        // Mock profile and friend data
        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setUser(new User());
        profile.getUser().setId(userId);
        profile.setVisibility(Profile.ProfileVisibility.PUBLIC);

        when(profileRepository.findByUserId(userId)).thenReturn(java.util.Optional.of(profile));
        when(friendService.areFriends(anyLong(), anyLong())).thenReturn(true);

        boolean isAccessible = profileService.isProfileAccessibleToUser(profileId, viewerId);
        assertTrue(isAccessible);
    }

    @Test
    void testIsProfileNotAccessible() {
        // Mock profile data
        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setUser(new User());
        profile.getUser().setId(userId);
        profile.setVisibility(Profile.ProfileVisibility.PRIVATE);

        when(profileRepository.findByUserId(userId)).thenReturn(java.util.Optional.of(profile));
        when(friendService.areFriends(anyLong(), anyLong())).thenReturn(false);

        boolean isAccessible = profileService.isProfileAccessibleToUser(profileId, viewerId);
        assertFalse(isAccessible);
    }

    @Test
    void testCreateProfile() {
        // Mock user data
        User user = new User();
        user.setId(userId);

        when(userSearchService.getUserEntityById(userId)).thenReturn(user);

        // Mock profile and wardrobe data
        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setUser(user);

        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        Profile createdProfile = profileService.createProfile(userId, "Test Bio", Profile.ProfileVisibility.PUBLIC);

        assertNotNull(createdProfile);
        assertEquals(userId, createdProfile.getUser().getId());
    }

    @Test
    void testCreateProfileUserNotFound() {
        when(userSearchService.getUserEntityById(userId)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> profileService.createProfile(userId, "Test Bio", Profile.ProfileVisibility.PUBLIC));

        assertEquals("User not found with ID: " + userId, exception.getMessage());
    }

    @Test
    void testAddWardrobeToProfile() {
        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setUser(new User());
        profile.getUser().setId(userId);

        Wardrobe wardrobe = new Wardrobe();
        wardrobe.setId(1L);
        wardrobe.setName("Test Wardrobe");

        when(profileRepository.findById(profileId)).thenReturn(java.util.Optional.of(profile));
        when(wardrobeRepository.save(any(Wardrobe.class))).thenReturn(wardrobe);

        Wardrobe addedWardrobe = profileService.addWardrobeToProfile(profileId, "Test Wardrobe");

        assertNotNull(addedWardrobe);
        assertEquals("Test Wardrobe", addedWardrobe.getName());
    }

    @Test
    void testMoveItemToAnotherWardrobe() {
        Item item = new Item();
        item.setId(1L);

        Wardrobe newWardrobe = new Wardrobe();
        newWardrobe.setId(2L);

        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
        when(wardrobeRepository.findById(2L)).thenReturn(java.util.Optional.of(newWardrobe));

        profileService.moveItemToAnotherWardrobe(1L, 2L);

        assertEquals(newWardrobe, item.getWardrobe());
    }
}
