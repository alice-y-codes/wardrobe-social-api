package com.yalice.wardrobe_social_app.services.userServiceTests;

import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.services.UserDetailsServiceImpl;
import com.yalice.wardrobe_social_app.services.helpers.DtoConversionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DtoConversionService dtoConversionService;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");  // In practice, this would be a hashed password
    }

    @Test
    void testLoadUserByUsername_Success() {
        // Mock repository to return the user when searching by username
        when(userRepository.findByUsername("testuser")).thenReturn(java.util.Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Mock repository to return empty when searching by username
        when(userRepository.findByUsername("testuser")).thenReturn(java.util.Optional.empty());

        // Ensure that the UsernameNotFoundException is thrown
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("testuser");
        });

        assertEquals("User not found with username: testuser", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("testuser");
    }
}
