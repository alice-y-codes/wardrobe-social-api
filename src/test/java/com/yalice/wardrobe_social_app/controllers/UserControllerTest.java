package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.configs.TestSecurityConfig;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.exceptions.UserRegistrationException;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {

        private MockMvc mockMvc;

        @Mock
        private UserService userService;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @InjectMocks
        private UserController userController;

        private User user;

        @BeforeEach
        public void setup() {
                MockitoAnnotations.openMocks(this); // Initialize mocks

                // Setup UserController with dependencies
                mockMvc = MockMvcBuilders.standaloneSetup(userController)
                        .setControllerAdvice(new GlobalExceptionHandler())
                        .build();

                // Initialize a sample user for testing
                initializeTestUser();
        }

        private void initializeTestUser() {
                user = new User();
                user.setUsername("alice");
                user.setPassword("password123"); // Ensure password meets length requirement
                user.setProfilePicture("https://example.com/alice-profile.jpg");
                user.setEmail("alice@example.com");
                user.setProvider("google");
        }

        @Test
        public void shouldRegisterUser() throws Exception {
                // Arrange
                Mockito.when(userService.registerUser(any(User.class))).thenReturn(Optional.of(user));

                // Act & Assert
                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("alice"))
                                .andExpect(jsonPath("$.profilePicture").value("https://example.com/alice-profile.jpg"))
                                .andExpect(jsonPath("$.email").value("alice@example.com"))
                                .andExpect(jsonPath("$.provider").value("google"));

                Mockito.verify(userService, Mockito.times(1)).registerUser(any(User.class));
        }

        @Test
        public void shouldReturnBadRequestWhenRegisteringUserWithMissingFields() throws Exception {
                User incompleteUser = new User();
                incompleteUser.setUsername("alice");
                incompleteUser.setPassword("password123"); // Missing email and provider

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(incompleteUser)))
                                .andExpect(status().isBadRequest());

                Mockito.verify(userService, Mockito.never()).registerUser(any(User.class));
        }

        @Test
        public void shouldReturnBadRequestWhenEmailIsEmpty() throws Exception {
                User userWithEmptyEmail = new User();
                userWithEmptyEmail.setUsername("alice");
                userWithEmptyEmail.setPassword("password123");
                userWithEmptyEmail.setProfilePicture("https://example.com/alice-profile.jpg");
                userWithEmptyEmail.setEmail("");
                userWithEmptyEmail.setProvider("google");

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userWithEmptyEmail)))
                                .andExpect(status().isBadRequest());

                Mockito.verify(userService, Mockito.never()).registerUser(any(User.class));
        }

        @Test
        public void shouldReturnBadRequestWhenProviderIsEmpty() throws Exception {
                User userWithEmptyProvider = new User();
                userWithEmptyProvider.setUsername("alice");
                userWithEmptyProvider.setPassword("password123");
                userWithEmptyProvider.setProfilePicture("https://example.com/alice-profile.jpg");
                userWithEmptyProvider.setEmail("alice@testmail.com");
                userWithEmptyProvider.setProvider("");

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userWithEmptyProvider)))
                                .andExpect(status().isBadRequest());

                Mockito.verify(userService, Mockito.never()).registerUser(any(User.class));
        }

        @Test
        public void shouldReturnBadRequestWhenPasswordTooShort() throws Exception {
                User userWithShortPassword = new User();
                userWithShortPassword.setUsername("alice");
                userWithShortPassword.setPassword("short"); // Less than 8 characters
                userWithShortPassword.setProfilePicture("https://example.com/alice-profile.jpg");
                userWithShortPassword.setEmail("alice@testmail.com");
                userWithShortPassword.setProvider("google");

                // Service will throw exception for business rule violation
                Mockito.when(userService.registerUser(any(User.class)))
                                .thenThrow(new UserRegistrationException(
                                                "Password must be at least 8 characters long"));

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userWithShortPassword)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Password must be at least 8 characters long"));

                Mockito.verify(userService, Mockito.times(1)).registerUser(any(User.class));
        }

        @Test
        public void shouldReturnBadRequestWhenProviderIsInvalid() throws Exception {
                User userWithInvalidProvider = new User();
                userWithInvalidProvider.setUsername("alice");
                userWithInvalidProvider.setPassword("password123");
                userWithInvalidProvider.setProfilePicture("https://example.com/alice-profile.jpg");
                userWithInvalidProvider.setEmail("alice@testmail.com");
                userWithInvalidProvider.setProvider("invalid_provider");

                // Service will throw exception for business rule violation
                Mockito.when(userService.registerUser(any(User.class)))
                                .thenThrow(new UserRegistrationException("Provider is not valid"));

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userWithInvalidProvider)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").value("Provider is not valid"));

                Mockito.verify(userService, Mockito.times(1)).registerUser(any(User.class));
        }

        @Test
        public void shouldReturnConflictWhenUsernameIsTaken() throws Exception {
                // Service returns empty Optional to indicate username is taken
                Mockito.when(userService.registerUser(any(User.class))).thenReturn(Optional.empty());

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.message").value("Username already taken"));

                Mockito.verify(userService, Mockito.times(1)).registerUser(any(User.class));
        }

        @Test
        public void shouldFindUser() throws Exception {
                // Arrange
                Mockito.when(userService.findUserByUsername(eq("alice"))).thenReturn(Optional.of(user));

                // Act & Assert
                mockMvc.perform(get("/api/users/findByUsername")
                                .param("username", "alice")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("alice"))
                                .andExpect(jsonPath("$.email").value("alice@example.com"))
                                .andExpect(jsonPath("$.provider").value("google"));
        }

        @Test
        public void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
                // Arrange
                Mockito.when(userService.findUserByUsername(eq("nonexistent"))).thenReturn(Optional.empty());

                // Act & Assert
                mockMvc.perform(get("/api/users/findByUsername")
                                .param("username", "nonexistent")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }
}
