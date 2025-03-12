package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeDto;
import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeResponseDto;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.WardrobeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WardrobeControllerTest {

    @Mock
    private WardrobeService wardrobeService;

    @Mock
    private AuthUtils authUtils;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;
    private Profile testProfile;

    @InjectMocks
    private WardrobeController wardrobeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(wardrobeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        testProfile = Profile.builder().id(1L).build();
        testUser = User.builder().id(1L).profile(testProfile).build();
    }

    @Test
    void createWardrobe() throws Exception {
        WardrobeDto wardrobeDto = WardrobeDto.builder()
                .name("Test Wardrobe")
                .build();

        WardrobeResponseDto responseDto = WardrobeResponseDto.builder()
                .id(1L)
                .name("Test Wardrobe")
                .profileId(1L)
                .build();

        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(wardrobeService.createWardrobe(any(), any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/wardrobes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wardrobeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void createWardrobe_Unauthorized() throws Exception {
        WardrobeDto wardrobeDto = new WardrobeDto();
        when(authUtils.getCurrentUserOrElseThrow())
                .thenThrow(new SecurityException("Unauthorized"));

        mockMvc.perform(post("/api/wardrobes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wardrobeDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateWardrobe() throws Exception {
        Long wardrobeId = 1L;
        WardrobeDto wardrobeDto = WardrobeDto.builder()
                .name("Updated Wardrobe")
                .build();

        WardrobeResponseDto responseDto = WardrobeResponseDto.builder()
                .id(wardrobeId)
                .name("Updated Wardrobe")
                .profileId(1L)
                .build();

        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(wardrobeService.updateWardrobe(any(), any())).thenReturn(responseDto);

        mockMvc.perform(put("/api/wardrobes/{wardrobeId}", wardrobeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wardrobeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void updateWardrobe_NotFound() throws Exception {
        Long wardrobeId = 1L;
        WardrobeDto wardrobeDto = new WardrobeDto();

        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(wardrobeService.updateWardrobe(any(), any()))
                .thenThrow(new ResourceNotFoundException("Wardrobe not found"));

        mockMvc.perform(put("/api/wardrobes/{wardrobeId}", wardrobeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wardrobeDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Wardrobe not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void deleteWardrobe() throws Exception {
        Long wardrobeId = 1L;
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(wardrobeService.deleteWardrobe(any())).thenReturn(true);

        mockMvc.perform(delete("/api/wardrobes/{wardrobeId}", wardrobeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteWardrobe_NotFound() throws Exception {
        Long wardrobeId = 1L;
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(wardrobeService.deleteWardrobe(any()))
                .thenThrow(new ResourceNotFoundException("Wardrobe not found"));

        mockMvc.perform(delete("/api/wardrobes/{wardrobeId}", wardrobeId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Wardrobe not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getWardrobeById() throws Exception {
        Long wardrobeId = 1L;
        WardrobeResponseDto responseDto = WardrobeResponseDto.builder()
                .id(wardrobeId)
                .name("Test Wardrobe")
                .profileId(1L)
                .build();

        when(wardrobeService.getWardrobeById(any())).thenReturn(responseDto);

        mockMvc.perform(get("/api/wardrobes/{wardrobeId}", wardrobeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void getWardrobeById_NotFound() throws Exception {
        Long wardrobeId = 1L;
        when(wardrobeService.getWardrobeById(any()))
                .thenThrow(new ResourceNotFoundException("Wardrobe not found"));

        mockMvc.perform(get("/api/wardrobes/{wardrobeId}", wardrobeId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Wardrobe not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getProfileWardrobes() throws Exception {
        List<WardrobeResponseDto> wardrobes = List.of(
                WardrobeResponseDto.builder()
                        .id(1L)
                        .name("Test Wardrobe")
                        .profileId(1L)
                        .build());

        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(wardrobeService.getProfileWardrobes(any())).thenReturn(wardrobes);

        mockMvc.perform(get("/api/wardrobes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void getProfileWardrobes_Empty() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(wardrobeService.getProfileWardrobes(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/wardrobes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void getProfileWardrobes_Unauthorized() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow())
                .thenThrow(new SecurityException("Unauthorized"));

        mockMvc.perform(get("/api/wardrobes"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
