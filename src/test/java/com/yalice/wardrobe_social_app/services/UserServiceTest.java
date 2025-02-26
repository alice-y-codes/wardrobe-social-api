package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.UserRegistrationException;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setProvider("google");
        user.setProfilePicture("https://example.com/profile.jpg");
        user.setPassword("password");
    }

    @Test
    public void shouldRegisterUser() {
        // Arrange
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        Optional<User> createdUser = userService.registerUser(user);

        // Assert
        assertThat(createdUser).isPresent();
        assertThat(createdUser.get().getUsername()).isEqualTo("testUser");
        assertThat(createdUser.get().getPassword()).isEqualTo("encodedPassword");
        verify(passwordEncoder).encode(any(String.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void shouldFindUserByUsername_WhenUserExists() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Act
        Optional<User> foundUser = userService.findUserByUsername("testUser");

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testUser");
        verify(userRepository).findByUsername("testUser");
    }

    @Test
    public void shouldNotFindUserByUsername_WhenNonExistentUser() {
        // Arrange
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(Optional.empty());

        // Act & Assert
        Optional<User> foundUser = userService.findUserByUsername("nonExistingUser");
        assertThat(foundUser).isEmpty();
        verify(userRepository).findByUsername("nonExistingUser");
    }

    @Test
    public void shouldNotRegisterUser_IfUsernameAlreadyExists() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThat(userService.registerUser(user)).isEmpty();
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void shouldHashPassword_BeforeSavingUser() {
        // Arrange
        when(passwordEncoder.encode(any(String.class))).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        Optional<User> createdUser = userService.registerUser(user);

        // Assert
        assertThat(createdUser).isPresent();
        assertThat(createdUser.get().getPassword()).isEqualTo("hashedPassword");
        verify(passwordEncoder).encode(eq("password"));
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void shouldThrowInvalidProvider_WhenProviderIsInvalid() {
        // Arrange
        user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setProvider(""); // Invalid provider
        user.setProfilePicture("https://example.com/profile.jpg");
        user.setPassword("password123");
        when(userRepository.save(user)).thenThrow(new UserRegistrationException("Provider is not valid"));

        // Act & Assert
        Exception thrown = assertThrows(UserRegistrationException.class, () -> {
            userService.registerUser(user);
        });

        assertThat(thrown.getMessage()).isEqualTo("Provider is not valid");
    }

    @Test
    public void shouldThrowInvalidPassword_WhenPasswordIsInvalid() {
        // Arrange
        user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setProvider("google");
        user.setProfilePicture("https://example.com/profile.jpg");
        user.setPassword("password");
        when(userRepository.save(user)).thenThrow(new UserRegistrationException("Password must be at least 8 characters long"));

        // Act & Assert
        Exception thrown = assertThrows(UserRegistrationException.class, () -> {
            userService.registerUser(user);
        });

        assertThat(thrown.getMessage()).isEqualTo("Password must be at least 8 characters long");
    }

}
