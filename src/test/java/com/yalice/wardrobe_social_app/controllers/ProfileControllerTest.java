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

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
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

        private static final String SUCCESS_PROFILE_RETRIEVED = "Profile retrieved successfully";
        private static final String SUCCESS_PROFILE_UPDATED = "Profile updated successfully";
        private static final String SUCCESS_PROFILE_VISIBILITY_UPDATED = "Profile visibility updated successfully";
        private static final String ERROR_MESSAGE = "Failed to update profile";

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders.standaloneSetup(profileController)
                        .setControllerAdvice(new GlobalExceptionHandler())
                        .build();

                initializeTestData();
        }

        private void initializeTestData() {
                testUser = User.builder().id(1L).username("testuser").password("testpassword").build();

                testProfileDto = new ProfileDto("Test bio", true, "Test Location", "Casual, Modern", "Brand1, Brand2", "Inspiration1, Inspiration2");

                testProfileResponseDto = new ProfileResponseDto(1L, 1L, "testuser", "Test bio", "Test Location", "Casual, Modern", "Brand1, Brand2", "Inspiration1, Inspiration2", "https://example.com/profile.jpg", true);

                testImageFile = new MockMultipartFile("image", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes());
        }

        private void mockAuthAndProfileService(User user, ProfileResponseDto profileResponseDto) {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(user);
                when(profileService.getProfile(anyLong())).thenReturn(profileResponseDto);
        }

        @Test
        void getMyProfile_Success() throws Exception {
                mockAuthAndProfileService(testUser, testProfileResponseDto);

                mockMvc.perform(get("/api/profiles/me"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", equalTo(true))) // Replaced 'is(true)' with 'equalTo(true)'
                        .andExpect(jsonPath("$.message", equalTo(SUCCESS_PROFILE_RETRIEVED)))
                        .andExpect(jsonPath("$.data.id", equalTo(1)))
                        .andExpect(jsonPath("$.data.username", equalTo("testuser")));

                verify(profileService).getProfile(anyLong());
        }

        @Test
        void getMyProfile_Error() throws Exception {
                mockAuthAndProfileService(testUser, testProfileResponseDto);
                when(profileService.getProfile(anyLong())).thenThrow(new RuntimeException("Profile retrieval failed"));

                mockMvc.perform(get("/api/profiles/me"))
                        .andExpect(status().isInternalServerError())
                        .andExpect(jsonPath("$.success", equalTo(false))) // Replaced 'is(false)' with 'equalTo(false)'
                        .andExpect(jsonPath("$.message", equalTo("Profile retrieval failed")));

                verify(profileService).getProfile(anyLong());
        }

        @Test
        void getProfile_Success() throws Exception {
                mockAuthAndProfileService(testUser, testProfileResponseDto);

                mockMvc.perform(get("/api/profiles/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", equalTo(true))) // Replaced 'is(true)' with 'equalTo(true)'
                        .andExpect(jsonPath("$.message", equalTo(SUCCESS_PROFILE_RETRIEVED)))
                        .andExpect(jsonPath("$.data.id", equalTo(1)));

                verify(profileService).getProfile(anyLong());
        }

        @Test
        void getProfile_NotFound() throws Exception {
                when(profileService.getProfile(anyLong())).thenThrow(new RuntimeException("Profile not found"));

                mockMvc.perform(get("/api/profiles/1"))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.success", equalTo(false))) // Replaced 'is(false)' with 'equalTo(false)'
                        .andExpect(jsonPath("$.message", equalTo("Profile not found")));

                verify(profileService).getProfile(anyLong());
        }

        @Test
        void updateProfile_Success() throws Exception {
                mockAuthAndProfileService(testUser, testProfileResponseDto);

                MockMultipartFile profilePart = new MockMultipartFile("profile", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(testProfileDto).getBytes());

                mockMvc.perform(multipart("/api/profiles/me")
                                .file(profilePart)
                                .file(testImageFile)
                                .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                }))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", equalTo(true))) // Replaced 'is(true)' with 'equalTo(true)'
                        .andExpect(jsonPath("$.message", equalTo(SUCCESS_PROFILE_UPDATED)))
                        .andExpect(jsonPath("$.data.id", equalTo(1)));

                verify(profileService).updateProfile(anyLong(), any(ProfileDto.class), any());
        }

        @Test
        void updateProfile_Error() throws Exception {
                mockAuthAndProfileService(testUser, testProfileResponseDto);
                when(profileService.updateProfile(anyLong(), any(ProfileDto.class), any())).thenThrow(new RuntimeException(ERROR_MESSAGE));

                MockMultipartFile profilePart = new MockMultipartFile("profile", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(testProfileDto).getBytes());

                mockMvc.perform(multipart("/api/profiles/me")
                                .file(profilePart)
                                .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                }))
                        .andExpect(status().isInternalServerError())
                        .andExpect(jsonPath("$.success", equalTo(false))) // Replaced 'is(false)' with 'equalTo(false)'
                        .andExpect(jsonPath("$.message", equalTo(ERROR_MESSAGE)));

                verify(profileService).updateProfile(anyLong(), any(ProfileDto.class), any());
        }

        @Test
        void updateProfileVisibility_Success() throws Exception {
                mockAuthAndProfileService(testUser, testProfileResponseDto);
                when(profileService.updateProfileVisibility(anyLong(), anyBoolean())).thenReturn(testProfileResponseDto);

                mockMvc.perform(put("/api/profiles/me/visibility")
                                .param("isPublic", "true"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", equalTo(true))) // Replaced 'is(true)' with 'equalTo(true)'
                        .andExpect(jsonPath("$.message", equalTo(SUCCESS_PROFILE_VISIBILITY_UPDATED)))
                        .andExpect(jsonPath("$.data.id", equalTo(1)))
                        .andExpect(jsonPath("$.data.isPublic", equalTo(true)));

                verify(profileService).updateProfileVisibility(anyLong(), anyBoolean());
        }

        @Test
        void updateProfileVisibility_Error() throws Exception {
                mockAuthAndProfileService(testUser, testProfileResponseDto);
                when(profileService.updateProfileVisibility(anyLong(), anyBoolean())).thenThrow(new RuntimeException("Failed to update profile visibility"));

                mockMvc.perform(put("/api/profiles/me/visibility")
                                .param("isPublic", "true"))
                        .andExpect(status().isInternalServerError())
                        .andExpect(jsonPath("$.success", equalTo(false))) // Replaced 'is(false)' with 'equalTo(false)'
                        .andExpect(jsonPath("$.message", equalTo("Failed to update profile visibility")));

                verify(profileService).updateProfileVisibility(anyLong(), anyBoolean());
        }
}
