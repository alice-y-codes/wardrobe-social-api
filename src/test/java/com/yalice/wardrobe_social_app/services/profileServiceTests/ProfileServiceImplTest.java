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
import com.yalice.wardrobe_social_app.services.helpers.DtoConversionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

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

    @Mock
    private DtoConversionService dtoConversionService;

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

        ProfileResponseDto profileResponseDto = new ProfileResponseDto();
        profileResponseDto.setId(profileId);
        profileResponseDto.setUserId(userId);
        profileResponseDto.setBio("New Bio");
        profileResponseDto.setLocation("New Location");
        when(dtoConversionService.convertToProfileResponseDto(profile)).thenReturn(profileResponseDto);

        ProfileResponseDto profileResponse = profileService.getProfile(userId);

        assertNotNull(profileResponse);
        assertEquals(userId, profileResponse.getUserId());
        verify(dtoConversionService, times(1)).convertToProfileResponseDto(profile);
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

        // Mock the profileRepository to return the existing profile
        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        // Create a ProfileDto with the new bio (data from the user request)
        ProfileDto profileDto = new ProfileDto();
        profileDto.setBio("New Bio");

        // Mock Image as MultipartFile (empty byte array in this case)
        MockMultipartFile image = new MockMultipartFile("image", "profile.jpg", "image/jpeg", new byte[]{});

        // Mock the saveAndFlush to simulate saving the updated profile
        // Ensure bio is updated in the mocked profile
        profile.setBio("New Bio");
        when(profileRepository.saveAndFlush(any(Profile.class))).thenReturn(profile);

        // Create a mock ProfileResponseDto with the updated bio
        ProfileResponseDto profileResponseDto = new ProfileResponseDto();
        profileResponseDto.setId(profileId);
        profileResponseDto.setUserId(userId);
        profileResponseDto.setBio("New Bio");

        // Mock dtoConversionService to return the mock ProfileResponseDto
        when(dtoConversionService.convertToProfileResponseDto(any(Profile.class))).thenReturn(profileResponseDto);

        // Call the method under test
        ProfileResponseDto updatedProfile = profileService.updateProfile(userId, profileDto, image);

        // Debugging output to check the result
        System.out.println("Updated Profile: " + updatedProfile);  // Debug print to check the returned value

        // Assert the result is not null and matches the expected updated bio
        assertNotNull(updatedProfile, "Updated profile should not be null");
        assertEquals("New Bio", updatedProfile.getBio(), "Bio should be updated to 'New Bio'");

        // Verify that saveAndFlush was called once to save the updated profile
        verify(profileRepository, times(1)).saveAndFlush(any(Profile.class));

        // Verify that dtoConversionService was called to convert the profile to ProfileResponseDto
        verify(dtoConversionService, times(1)).convertToProfileResponseDto(any(Profile.class));
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
        when(profileRepository.saveAndFlush(any(Profile.class))).thenReturn(profile);

        ProfileResponseDto profileResponseDto = new ProfileResponseDto();
        profileResponseDto.setId(profileId);
        profileResponseDto.setUserId(userId);
        profileResponseDto.setBio("New Bio");
        profileResponseDto.setLocation("New Location");
        profileResponseDto.setPublic(true);


        when(dtoConversionService.convertToProfileResponseDto(profile)).thenReturn(profileResponseDto);

        // Perform the test
        ProfileResponseDto updatedProfile = profileService.updateProfileVisibility(userId, true);

        assertNotNull(updatedProfile);
        assertTrue(updatedProfile.isPublic());
        verify(dtoConversionService, times(1)).convertToProfileResponseDto(profile);
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

        Long viewerId = 2L;  // Mock viewer ID that is different from the profile's user

        when(profileRepository.findByUserId(profileId)).thenReturn(Optional.of(profile));
        when(friendService.areFriends(profileId, viewerId)).thenReturn(false);

        boolean isAccessible = profileService.isProfileAccessibleToUser(profileId, viewerId);

        assertFalse(isAccessible, "Profile should not be accessible when private and not friends.");
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
