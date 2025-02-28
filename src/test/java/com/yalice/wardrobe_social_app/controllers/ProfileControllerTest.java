package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.dtos.ProfileUpdateRequest;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.Profile.ProfileVisibility;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.interfaces.FriendshipService;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileControllerTest {

        private MockMvc mockMvc;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Mock
        private ProfileService profileService;

        @Mock
        private UserService userService;

        @Mock
        private FriendshipService friendshipService;

        @InjectMocks
        private ProfileController profileController;

        private User testUser;
        private Profile testProfile;
        private ProfileUpdateRequest updateRequest;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders.standaloneSetup(profileController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();

                testUser = User.builder()
                                .id(1L)
                                .username("testuser")
                                .email("test@example.com")
                                .build();

                testProfile = Profile.builder()
                                .id(1L)
                                .user(testUser)
                                .bio("Test bio")
                                .visibility(ProfileVisibility.PUBLIC)
                                .build();

                updateRequest = new ProfileUpdateRequest();
                updateRequest.setBio("Updated bio");
                updateRequest.setVisibility(ProfileVisibility.FRIENDS_ONLY);
        }

        @AfterEach
        void tearDown() {
                SecurityContextHolder.clearContext();
        }

        @Test
        void getUserProfile_whenProfileExistsAndAccessible_returnsProfile() throws Exception {
                // Setup authentication
                setupAuthentication("testuser");

                when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
                when(profileService.getProfileByUserId(1L)).thenReturn(Optional.of(testProfile));
                when(profileService.isProfileAccessibleToUser(1L, 1L)).thenReturn(true);

                mockMvc.perform(get("/api/users/1/profile"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.bio").value("Test bio"))
                                .andExpect(jsonPath("$.visibility").value("PUBLIC"));
        }

        @Test
        void getUserProfile_whenProfileNotAccessible_returnsForbidden() throws Exception {
                // Setup authentication
                setupAuthentication("testuser");

                when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
                when(profileService.isProfileAccessibleToUser(2L, 1L)).thenReturn(false);

                mockMvc.perform(get("/api/users/2/profile"))
                                .andExpect(status().isForbidden());
        }

        @Test
        void getUserProfile_whenProfileNotFound_returnsNotFound() throws Exception {
                // Setup authentication
                setupAuthentication("testuser");

                when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
                when(profileService.getProfileByUserId(2L)).thenReturn(Optional.empty());
                when(profileService.isProfileAccessibleToUser(2L, 1L)).thenReturn(true);

                mockMvc.perform(get("/api/users/2/profile"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void updateProfile_whenUserIsProfileOwner_updatesAndReturnsProfile() throws Exception {
                // Setup authentication
                setupAuthentication("testuser");

                when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
                when(profileService.updateProfile(1L, "Updated bio", ProfileVisibility.FRIENDS_ONLY))
                                .thenReturn(testProfile);

                mockMvc.perform(put("/api/users/1/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        void updateProfile_whenUserIsNotProfileOwner_returnsForbidden() throws Exception {
                // Setup authentication
                setupAuthentication("testuser");

                when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));

                mockMvc.perform(put("/api/users/2/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isForbidden());
        }

        private void setupAuthentication(String username) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                username, null, new ArrayList<>());
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);
                SecurityContextHolder.setContext(securityContext);
        }
}