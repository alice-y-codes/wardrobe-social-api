package com.yalice.wardrobe_social_app.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class UserControllerTest {

        private MockMvc mockMvc;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Mock
        private UserService userService;

        @InjectMocks
        private UserController userController;

        private User user;

        @BeforeEach
        public void setup() {
                // Initialize mocks first
                MockitoAnnotations.openMocks(this);

                // Then set up the controller with the mocks
                mockMvc = MockMvcBuilders.standaloneSetup(userController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();

                initializeTestUser();
        }

        private void initializeTestUser() {
                user = User.builder()
                                .username("alice")
                                .password("password123")
                                .profilePicture("https://example.com/alice-profile.jpg")
                                .email("alice@example.com")
                                .provider(User.Provider.GOOGLE)
                                .build();
        }

        @Test
        public void shouldRegisterUser() throws Exception {
                // Arrange
                when(userService.registerUser(any(User.class))).thenReturn(Optional.of(user));

                // Act & Assert
                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("alice"))
                                .andExpect(jsonPath("$.profilePicture").value("https://example.com/alice-profile.jpg"))
                                .andExpect(jsonPath("$.email").value("alice@example.com"))
                                .andExpect(jsonPath("$.provider").value("GOOGLE"));

                verify(userService, Mockito.times(1)).registerUser(any(User.class));
        }

        @Test
        public void shouldReturnBadRequestWhenRegisteringUserWithMissingFields() throws Exception {
                User incompleteUser = User.builder()
                                .username("alice")
                                .password("password123")
                                .build();

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(incompleteUser)))
                                .andExpect(status().isBadRequest());

                verify(userService, Mockito.never()).registerUser(any(User.class));
        }

        @Test
        public void shouldReturnBadRequestWhenEmailIsEmpty() throws Exception {
                User userWithEmptyEmail = User.builder()
                                .username("alice")
                                .password("password123")
                                .provider(User.Provider.GOOGLE)
                                .email("")
                                .build();

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userWithEmptyEmail)))
                                .andExpect(status().isBadRequest());

                verify(userService, Mockito.never()).registerUser(any(User.class));
        }

        @Test
        public void shouldReturnBadRequestWhenProviderIsEmpty() throws Exception {
                User userWithEmptyProvider = User.builder()
                                .username("alice")
                                .password("password123")
                                .email("alice@example.com")
                                .provider(null)
                                .build();

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userWithEmptyProvider)))
                                .andExpect(status().isBadRequest());

                verify(userService, Mockito.never()).registerUser(any(User.class));
        }

        @Test
        public void shouldReturnBadRequestWhenPasswordTooShort() throws Exception {
                User userWithShortPassword = User.builder()
                                .username("alice")
                                .password("short")
                                .email("alice@example.com")
                                .provider(User.Provider.GOOGLE)
                                .build();

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userWithShortPassword)))
                                .andExpect(status().isBadRequest());

                verify(userService, Mockito.never()).registerUser(any(User.class));
        }

        @Test
        public void shouldReturnConflictWhenUsernameIsTaken() throws Exception {
                when(userService.registerUser(any(User.class))).thenReturn(Optional.empty());

                mockMvc.perform(post("/api/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user)))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.message").value("Username already taken"));

                verify(userService, Mockito.times(1)).registerUser(any(User.class));
        }

        @Test
        public void shouldFindUser() throws Exception {
                // Arrange
                when(userService.findUserByUsername(eq("alice"))).thenReturn(Optional.of(user));

                // Act & Assert
                mockMvc.perform(get("/api/users/findByUsername")
                                .param("username", "alice")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("alice"))
                                .andExpect(jsonPath("$.email").value("alice@example.com"))
                                .andExpect(jsonPath("$.provider").value("GOOGLE"));
        }

        @Test
        public void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
                // Arrange
                when(userService.findUserByUsername(eq("nonexistent"))).thenReturn(Optional.empty());

                // Act & Assert
                mockMvc.perform(get("/api/users/findByUsername")
                                .param("username", "nonexistent")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }
}
