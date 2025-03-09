package com.yalice.wardrobe_social_app.services.userServiceTests;

import com.yalice.wardrobe_social_app.dtos.user.ChangePasswordDto;
import com.yalice.wardrobe_social_app.dtos.user.UserProfileDto;
import com.yalice.wardrobe_social_app.dtos.user.UserRegistrationDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.exceptions.UsernameAlreadyExistsException;
import com.yalice.wardrobe_social_app.mappers.UserMapper;
import com.yalice.wardrobe_social_app.repositories.ProfileRepository;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.services.UserManagementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserManagementServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserManagementServiceImpl userManagementService;

    private UserRegistrationDto registrationDto;
    private UserProfileDto profileDto;
    private ChangePasswordDto passwordDto;
    private User user;
    private Profile profile;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        registrationDto = new UserRegistrationDto("testuser", "test@example.com", "password123");

        profileDto = new UserProfileDto();
        profileDto.setBio("Test bio");
        profileDto.setLocation("Test location");

        passwordDto = new ChangePasswordDto("oldpassword", "newpassword");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedpassword");

        profile = new Profile();
        profile.setUser(user);
        profile.setBio("Test bio");
        profile.setLocation("Test location");
        profile.setVisibility(Profile.ProfileVisibility.PUBLIC);
        user.setProfile(profile);

        userResponseDto = new UserResponseDto(1L, "testuser", "test@example.com");
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.existsByUsername(registrationDto.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registrationDto.getPassword())).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);
        when(userMapper.toResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userManagementService.registerUser(registrationDto);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());

        verify(userRepository).save(any(User.class));
        verify(profileRepository).save(any(Profile.class));
        verify(userMapper).toResponseDto(any(User.class));
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists() {
        when(userRepository.existsByUsername(registrationDto.getUsername())).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> userManagementService.registerUser(registrationDto));

        verify(userRepository, never()).save(any(User.class));
        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    void testUpdateUserProfile_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);
        when(userMapper.toResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userManagementService.updateUserProfile(1L, profileDto);

        assertNotNull(result);
        verify(profileRepository).save(any(Profile.class));
        verify(userMapper).toResponseDto(user);

        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(profileCaptor.capture());
        Profile savedProfile = profileCaptor.getValue();
        assertEquals(profileDto.getBio(), savedProfile.getBio());
        assertEquals(profileDto.getLocation(), savedProfile.getLocation());
    }

    @Test
    void testUpdateUserProfile_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userManagementService.updateUserProfile(1L, profileDto));

        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    void testChangePassword_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(passwordDto.getNewPassword())).thenReturn("newencodedpassword");

        userManagementService.changePassword(1L, passwordDto);

        verify(userRepository).save(user);
        assertEquals("newencodedpassword", user.getPassword());
    }

    @Test
    void testChangePassword_InvalidOldPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())).thenReturn(false);

        assertThrows(SecurityException.class, () -> userManagementService.changePassword(1L, passwordDto));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userManagementService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userManagementService.deleteUser(1L));

        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void testExistsByUsername_True() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertTrue(userManagementService.existsByUsername("testuser"));
    }

    @Test
    void testExistsByUsername_False() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);

        assertFalse(userManagementService.existsByUsername("testuser"));
    }
}
