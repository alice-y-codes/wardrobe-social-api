package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        User createdUser = userService.registerUser(user);

        // Assert
        assertThat(createdUser.getUsername()).isEqualTo("testUser");
        assertThat(createdUser.getPassword()).isEqualTo("encodedPassword");
        verify(passwordEncoder).encode(any(String.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void shouldFindUserByUsername() {
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
    public void shouldFailToFindUserByUsername() {
        // Arrange
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(Optional.empty());

        // Act & Assert
        Optional<User> foundUser = userService.findUserByUsername("nonExistingUser");
        assertThat(foundUser).isEmpty();
        verify(userRepository).findByUsername("nonExistingUser");
    }

    @Test
    public void shouldNotRegisterUserIfUsernameAlreadyExists() {
        // Arrange
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThatThrownBy(() -> userService.registerUser(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username already taken");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void shouldNotRegisterUserWithNullUsername() {
        // Arrange
        user.setUsername(null);

        // Act & Assert
        assertThatThrownBy(() -> userService.registerUser(user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username cannot be null or empty");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void shouldHashPasswordBeforeSavingUser() {
        // Arrange
        when(passwordEncoder.encode(any(String.class))).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User createdUser = userService.registerUser(user);

        // Assert
        assertThat(createdUser.getPassword()).isEqualTo("hashedPassword");
        verify(passwordEncoder).encode(eq("password"));
        verify(userRepository).save(any(User.class));
    }
}
