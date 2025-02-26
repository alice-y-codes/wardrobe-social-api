package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        String rawPassword = "password";
        user.setPassword(rawPassword);
    }

    @Test
    public void shouldRegisterUser() {
        // Arrange
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User createdUser = userService.registerUser(user);

        //
        assertThat(createdUser.getUsername()).isEqualTo("testUser");
        assertThat(createdUser.getPassword()).isEqualTo("encodedPassword");
        verify(passwordEncoder).encode(any(String.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void shouldFindUserByUsername() {

    }
}
