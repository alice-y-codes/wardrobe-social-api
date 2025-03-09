package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.dtos.profile.ProfileDto;
import com.yalice.wardrobe_social_app.dtos.profile.ProfileResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileControllerTest {

        @Mock
        private ProfileService profileService;

        @Mock
        private AuthUtils authUtils;

        private MockMvc mockMvc;
        private ObjectMapper objectMapper;
        private User testUser;

        @InjectMocks
        private ProfileController profileController;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders
                                .standaloneSetup(profileController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();
                objectMapper = new ObjectMapper();
                testUser = User.builder().id(1L).build();
        }

        @Test
        void getMyProfile() throws Exception {
                ProfileResponseDto responseDto = createTestProfileResponse();
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(profileService.getProfile(eq(1L))).thenReturn(responseDto);

                mockMvc.perform(get("/api/profiles/me"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data").exists())
                                .andExpect(jsonPath("$.data.id").exists());
        }

        @Test
        void getMyProfile_Unauthorized() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow())
                                .thenThrow(new SecurityException("Unauthorized"));

                mockMvc.perform(get("/api/profiles/me"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Unauthorized"))
                                .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        void getProfile() throws Exception {
                ProfileResponseDto responseDto = createTestProfileResponse();
                when(profileService.getProfile(any())).thenReturn(responseDto);

                mockMvc.perform(get("/api/profiles/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data").exists())
                                .andExpect(jsonPath("$.data.id").exists());
        }

        @Test
        void getProfile_NotFound() throws Exception {
                when(profileService.getProfile(any()))
                                .thenThrow(new ResourceNotFoundException("Profile not found"));

                mockMvc.perform(get("/api/profiles/999"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Profile not found"))
                                .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        void updateProfile() throws Exception {
                ProfileResponseDto responseDto = createTestProfileResponse();
                ProfileDto profileDto = createTestProfileDto();
                MockMultipartFile profileJson = new MockMultipartFile(
                                "profile",
                                "",
                                "application/json",
                                objectMapper.writeValueAsString(profileDto).getBytes());
                MockMultipartFile image = new MockMultipartFile(
                                "image",
                                "test.jpg",
                                "image/jpeg",
                                "test image content".getBytes());

                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(profileService.updateProfile(any(), any(), any())).thenReturn(responseDto);

                mockMvc.perform(multipart("/api/profiles/me")
                                .file(profileJson)
                                .file(image)
                                .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                }))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data").exists());
        }

        @Test
        void updateProfile_Unauthorized() throws Exception {
                ProfileDto profileDto = createTestProfileDto();
                MockMultipartFile profileJson = new MockMultipartFile(
                                "profile",
                                "",
                                "application/json",
                                objectMapper.writeValueAsString(profileDto).getBytes());

                when(authUtils.getCurrentUserOrElseThrow())
                                .thenThrow(new SecurityException("Unauthorized"));

                mockMvc.perform(multipart("/api/profiles/me")
                                .file(profileJson)
                                .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                }))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Unauthorized"))
                                .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        void updateProfileVisibility() throws Exception {
                ProfileResponseDto responseDto = createTestProfileResponse();
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(profileService.updateProfileVisibility(eq(1L), eq(true))).thenReturn(responseDto);

                mockMvc.perform(put("/api/profiles/me/visibility")
                                .param("isPublic", "true"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data").exists());
        }

        @Test
        void updateProfileVisibility_Unauthorized() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow())
                                .thenThrow(new SecurityException("Unauthorized"));

                mockMvc.perform(put("/api/profiles/me/visibility")
                                .param("isPublic", "true"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Unauthorized"))
                                .andExpect(jsonPath("$.data").doesNotExist());
        }

        private ProfileResponseDto createTestProfileResponse() {
                return ProfileResponseDto.builder()
                                .id(1L)
                                .userId(1L)
                                .username("testuser")
                                .bio("Test bio")
                                .location("Test location")
                                .stylePreferences("Casual")
                                .favoriteBrands("Test brands")
                                .fashionInspirations("Test inspirations")
                                .profileImageUrl("https://example.com/image.jpg")
                                .isPublic(true)
                                .build();
        }

        private ProfileDto createTestProfileDto() {
                return ProfileDto.builder()
                                .bio("Test bio")
                                .location("Test location")
                                .stylePreferences("Casual")
                                .favoriteBrands("Test brands")
                                .fashionInspirations("Test inspirations")
                                .build();
        }
}
