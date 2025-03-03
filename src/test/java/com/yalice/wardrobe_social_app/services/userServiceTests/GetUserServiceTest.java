package com.yalice.wardrobe_social_app.services.userServiceTests;

import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.services.UserServiceImpl;
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

public class GetUserServiceTest {

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
        user = User.builder()
                .username("testUser")
                .email("test@example.com")
                .provider(User.Provider.GOOGLE)
                .profilePicture("https://example.com/profile.jpg")
                .password("password")
                .build();
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
}
