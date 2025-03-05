//package com.yalice.wardrobe_social_app.services.userServiceTests;
//
//import com.yalice.wardrobe_social_app.entities.User;
//import com.yalice.wardrobe_social_app.exceptions.UserRegistrationException;
//import com.yalice.wardrobe_social_app.repositories.UserRepository;
//import com.yalice.wardrobe_social_app.services.UserSearchServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//public class RegisterUserSearchServiceTest {
//
//    @InjectMocks
//    private UserSearchServiceImpl userService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private BCryptPasswordEncoder passwordEncoder;
//
//    private User user;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//        user = User.builder()
//                .username("testUser")
//                .email("test@example.com")
//                .provider(User.Provider.GOOGLE)
//                .profilePicture("https://example.com/profile.jpg")
//                .password("password")
//                .build();
//    }
//
//    @Test
//    public void shouldRegisterUser() {
//        // Arrange
//        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
//        when(userRepository.save(any(User.class))).thenReturn(user);
//
//        // Act
//        Optional<User> createdUser = userService.registerUser(user);
//
//        // Assert
//        assertThat(createdUser).isPresent();
//        assertThat(createdUser.get().getUsername()).isEqualTo("testUser");
//        assertThat(createdUser.get().getPassword()).isEqualTo("encodedPassword");
//        verify(passwordEncoder).encode(any(String.class));
//        verify(userRepository).save(any(User.class));
//    }
//
//    @Test
//    public void shouldNotRegisterUser_IfUsernameAlreadyExists() {
//        // Arrange
//        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
//
//        // Act & Assert
//        assertThat(userService.registerUser(user)).isEmpty();
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    public void shouldHashPassword_BeforeSavingUser() {
//        // Arrange
//        when(passwordEncoder.encode(any(String.class))).thenReturn("hashedPassword");
//        when(userRepository.save(any(User.class))).thenReturn(user);
//
//        // Act
//        Optional<User> createdUser = userService.registerUser(user);
//
//        // Assert
//        assertThat(createdUser).isPresent();
//        assertThat(createdUser.get().getPassword()).isEqualTo("hashedPassword");
//        verify(passwordEncoder).encode(eq("password"));
//        verify(userRepository).save(any(User.class));
//    }
//
//    @Test
//    public void shouldThrowInvalidPassword_WhenPasswordIsInvalid() {
//        // Arrange
//        user = new User();
//        user.setUsername("testUser");
//        user.setEmail("test@example.com");
//        user.setProvider(User.Provider.GOOGLE);
//        user.setProfilePicture("https://example.com/profile.jpg");
//        user.setPassword("password");
//        when(userRepository.save(user))
//                .thenThrow(new UserRegistrationException("Password must be at least 8 characters long"));
//
//        // Act & Assert
//        Exception thrown = assertThrows(UserRegistrationException.class, () -> {
//            userService.registerUser(user);
//        });
//
//        assertThat(thrown.getMessage()).isEqualTo("Password must be at least 8 characters long");
//    }
//
//    // Here you could add tests for more business validations:
//    // - Email format validation
//    // - Password complexity rules
//    // - Username format rules
//    // - etc.
//}
