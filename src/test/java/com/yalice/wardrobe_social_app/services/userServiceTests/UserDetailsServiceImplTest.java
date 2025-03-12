package com.yalice.wardrobe_social_app.services.userServiceTests;

import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("testuser")
                .password("password123") // Typically, this would be a hashed password
                .build();
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("testuser")
        );

        assertEquals("User not found with username: testuser", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("testuser");
    }
}
