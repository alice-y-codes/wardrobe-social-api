package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.dtos.profile.ProfileDto;
import com.yalice.wardrobe_social_app.dtos.profile.ProfileResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProfileControllerTest {

        private MockMvc mockMvc;

        @Mock
        private ProfileService profileService;

        @Mock
        private AuthUtils authUtils;

        @InjectMocks
        private ProfileController profileController;

        private final ObjectMapper objectMapper = new ObjectMapper();
        private User testUser;
        private ProfileDto testProfileDto;
        private ProfileResponseDto testProfileResponseDto;
        private MockMultipartFile testImageFile;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders.standaloneSetup(profileController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();

                initializeTestData();
        }

        private void initializeTestData() {
                testUser = User.builder()
                                .id(1L)
                                .username("testuser")
                                .email("test@example.com")
                                .build();

                testProfileDto = new ProfileDto();
                testProfileDto.setBio("Test bio");
                testProfileDto.setPublic(true);
                testProfileDto.setLocation("Test Location");
                testProfileDto.setStylePreferences("Casual, Modern");
                testProfileDto.setFavoriteBrands("Brand1, Brand2");
                testProfileDto.setFashionInspirations("Inspiration1, Inspiration2");

                testProfileResponseDto = ProfileResponseDto.builder()
                                .id(1L)
                                .userId(1L)
                                .username("testuser")
                                .bio("Test bio")
                                .location("Test Location")
                                .stylePreferences("Casual, Modern")
                                .favoriteBrands("Brand1, Brand2")
                                .fashionInspirations("Inspiration1, Inspiration2")
                                .profileImageUrl("https://example.com/profile.jpg")
                                .isPublic(true)
                                .build();

                testImageFile = new MockMultipartFile(
                                "image",
                                "test.jpg",
                                MediaType.IMAGE_JPEG_VALUE,
                                "test image content".getBytes());
        }

        @Test
        void getMyProfile_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(profileService.getProfile(anyLong())).thenReturn(testProfileResponseDto);

                mockMvc.perform(get("/api/profiles/me"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Profile retrieved successfully")))
                                .andExpect(jsonPath("$.data.id", is(1)))
                                .andExpect(jsonPath("$.data.username", is("testuser")))
                                .andExpect(jsonPath("$.data.bio", is("Test bio")));

                verify(profileService).getProfile(anyLong());
        }

        @Test
        void getMyProfile_Error() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(profileService.getProfile(anyLong()))
                                .thenThrow(new RuntimeException("Failed to retrieve profile"));

                mockMvc.perform(get("/api/profiles/me"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.success", is(false)))
                                .andExpect(jsonPath("$.message", is("Failed to retrieve profile")));

                verify(profileService).getProfile(anyLong());
        }

        @Test
        void getProfile_Success() throws Exception {
                when(profileService.getProfile(anyLong())).thenReturn(testProfileResponseDto);

                mockMvc.perform(get("/api/profiles/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Profile retrieved successfully")))
                                .andExpect(jsonPath("$.data.id", is(1)))
                                .andExpect(jsonPath("$.data.username", is("testuser")))
                                .andExpect(jsonPath("$.data.bio", is("Test bio")));

                verify(profileService).getProfile(anyLong());
        }

        @Test
        void getProfile_NotFound() throws Exception {
                when(profileService.getProfile(anyLong())).thenThrow(new RuntimeException("Profile not found"));

                mockMvc.perform(get("/api/profiles/1"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.success", is(false)))
                                .andExpect(jsonPath("$.message", is("Profile not found for user ID: 1")));

                verify(profileService).getProfile(anyLong());
        }

        @Test
        void updateProfile_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(profileService.updateProfile(anyLong(), any(ProfileDto.class), any()))
                                .thenReturn(testProfileResponseDto);

                MockMultipartFile profilePart = new MockMultipartFile(
                                "profile",
                                "",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(testProfileDto).getBytes());

                mockMvc.perform(multipart("/api/profiles/me")
                                .file(profilePart)
                                .file(testImageFile)
                                .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                }))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Profile updated successfully")))
                                .andExpect(jsonPath("$.data.id", is(1)))
                                .andExpect(jsonPath("$.data.username", is("testuser")))
                                .andExpect(jsonPath("$.data.bio", is("Test bio")));

                verify(profileService).updateProfile(anyLong(), any(ProfileDto.class), any());
        }

        @Test
        void updateProfile_WithoutImage_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(profileService.updateProfile(anyLong(), any(ProfileDto.class), any()))
                                .thenReturn(testProfileResponseDto);

                MockMultipartFile profilePart = new MockMultipartFile(
                                "profile",
                                "",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(testProfileDto).getBytes());

                mockMvc.perform(multipart("/api/profiles/me")
                                .file(profilePart)
                                .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                }))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Profile updated successfully")))
                                .andExpect(jsonPath("$.data.id", is(1)))
                                .andExpect(jsonPath("$.data.username", is("testuser")));

                verify(profileService).updateProfile(anyLong(), any(ProfileDto.class), any());
        }

        @Test
        void updateProfile_Error() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(profileService.updateProfile(anyLong(), any(ProfileDto.class), any()))
                                .thenThrow(new RuntimeException("Failed to update profile"));

                MockMultipartFile profilePart = new MockMultipartFile(
                                "profile",
                                "",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(testProfileDto).getBytes());

                mockMvc.perform(multipart("/api/profiles/me")
                                .file(profilePart)
                                .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                }))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.success", is(false)))
                                .andExpect(jsonPath("$.message", is("Failed to update profile")));

                verify(profileService).updateProfile(anyLong(), any(ProfileDto.class), any());
        }

        @Test
        void updateProfileVisibility_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(profileService.updateProfileVisibility(anyLong(), anyBoolean()))
                                .thenReturn(testProfileResponseDto);

                mockMvc.perform(put("/api/profiles/me/visibility")
                                .param("isPublic", "true"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Profile visibility updated successfully")))
                                .andExpect(jsonPath("$.data.id", is(1)))
                                .andExpect(jsonPath("$.data.isPublic", is(true)));

                verify(profileService).updateProfileVisibility(anyLong(), anyBoolean());
        }

        @Test
        void updateProfileVisibility_Error() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(profileService.updateProfileVisibility(anyLong(), anyBoolean()))
                                .thenThrow(new RuntimeException("Failed to update profile visibility"));

                mockMvc.perform(put("/api/profiles/me/visibility")
                                .param("isPublic", "true"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.success", is(false)))
                                .andExpect(jsonPath("$.message", is("Failed to update profile visibility")));

                verify(profileService).updateProfileVisibility(anyLong(), anyBoolean());
        }
}