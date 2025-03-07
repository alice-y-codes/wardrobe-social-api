package com.yalice.wardrobe_social_app.services.userServiceTests;

import com.yalice.wardrobe_social_app.dtos.user.UserDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.UserNotFoundException;
import com.yalice.wardrobe_social_app.exceptions.UsernameAlreadyExistsException;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.services.UserManagementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserManagementServiceImpl userManagementService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDto = new UserDto("testuser", "test@example.com", "password123");
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedpassword");
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDto userResponseDto = userManagementService.registerUser(userDto);

        assertNotNull(userResponseDto);
        assertEquals("testuser", userResponseDto.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists() {
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.of(user));

        UsernameAlreadyExistsException exception = assertThrows(UsernameAlreadyExistsException.class, () -> {
            userManagementService.registerUser(userDto);
        });

        assertEquals("Username already exists: testuser", exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void testUpdateUserProfile_Success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto updatedUserDto = new UserDto("newusername", "newemail@example.com", null);
        UserResponseDto updatedUserResponseDto = userManagementService.updateUserProfile(user.getId(), updatedUserDto);

        assertNotNull(updatedUserResponseDto);
        assertEquals("newusername", updatedUserResponseDto.getUsername());
        assertEquals("newemail@example.com", updatedUserResponseDto.getEmail());
    }

    @Test
    void testUpdateUserProfile_UserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userManagementService.updateUserProfile(user.getId(), userDto);
        });

        assertEquals("User not found with ID: 1", exception.getMessage());
    }

    @Test
    void testChangePassword_Success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodednewpassword");

        userManagementService.changePassword(user.getId(), "password123", "newpassword");

        assertEquals("encodednewpassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testChangePassword_IncorrectOldPassword() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", user.getPassword())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userManagementService.changePassword(user.getId(), "wrongpassword", "newpassword");
        });

        assertEquals("Incorrect old password.", exception.getMessage());
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userManagementService.deleteUser(user.getId());

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userManagementService.deleteUser(user.getId());
        });

        assertEquals("User not found with ID: 1", exception.getMessage());
    }
}
