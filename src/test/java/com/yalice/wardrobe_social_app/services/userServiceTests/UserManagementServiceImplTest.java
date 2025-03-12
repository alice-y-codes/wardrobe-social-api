package com.yalice.wardrobe_social_app.services.userServiceTests;

import com.yalice.wardrobe_social_app.dtos.user.ChangePasswordDto;
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
    private ChangePasswordDto passwordDto;
    private User user;
    private Profile profile;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        registrationDto = new UserRegistrationDto("testuser", "test@example.com", "password123", User.Provider.GOOGLE);

        passwordDto = new ChangePasswordDto("oldpassword", "newpassword");

        profile = Profile.builder()
                .bio("")
                .location("")
                .visibility(Profile.ProfileVisibility.PUBLIC)
                .user(user)
                .build();

        user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedpassword")
                .provider(User.Provider.LOCAL)
                .profile(profile)
                .build();

        userResponseDto = new UserResponseDto(1L, "testuser", "test@example.com");
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        when(userRepository.existsByUsername(registrationDto.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registrationDto.getPassword())).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);
        when(userMapper.toResponseDto(user)).thenReturn(userResponseDto);

        // Act
        UserResponseDto result = userManagementService.registerUser(registrationDto);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());

        // Verify exact objects passed to save methods
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("encodedpassword", savedUser.getPassword());

        ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(profileCaptor.capture());
        Profile savedProfile = profileCaptor.getValue();
        assertEquals("", savedProfile.getBio());
        assertEquals("", savedProfile.getLocation());
        assertEquals(Profile.ProfileVisibility.PUBLIC, savedProfile.getVisibility());

        verify(userMapper).toResponseDto(user);
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists() {
        when(userRepository.existsByUsername(registrationDto.getUsername())).thenReturn(true);

        // Act & Assert
        assertThrows(UsernameAlreadyExistsException.class, () -> userManagementService.registerUser(registrationDto));

        // Verify no save call was made
        verify(userRepository, never()).save(any(User.class));
        verify(profileRepository, never()).save(any(Profile.class));
    }

    @Test
    void testChangePassword_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(passwordDto.getNewPassword())).thenReturn("newencodedpassword");

        // Act
        userManagementService.changePassword(1L, passwordDto);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("newencodedpassword", savedUser.getPassword());
    }

    @Test
    void testChangePassword_InvalidOldPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(passwordDto.getOldPassword(), user.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(SecurityException.class, () -> userManagementService.changePassword(1L, passwordDto));

        // Verify no save call was made
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        // Act
        userManagementService.deleteUser(1L);

        // Assert
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userManagementService.deleteUser(1L));

        // Verify no delete call was made
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void testExistsByUsername_True() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        assertTrue(userManagementService.existsByUsername("testuser"));
    }

    @Test
    void testExistsByUsername_False() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);

        // Act & Assert
        assertFalse(userManagementService.existsByUsername("testuser"));
    }
}
