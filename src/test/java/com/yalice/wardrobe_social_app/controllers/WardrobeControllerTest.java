package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeDto;
import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.WardrobeService;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WardrobeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WardrobeService wardrobeService;

    @Mock
    private AuthUtils authUtils;

    @InjectMocks
    private WardrobeController wardrobeController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private WardrobeDto wardrobeDto;
    private WardrobeResponseDto wardrobeResponseDto;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(wardrobeController)
                .setControllerAdvice(new GlobalExceptionHandler())  // Adding the global exception handler
                .build();

        // Initialize test data
        wardrobeDto = WardrobeDto.builder()
                .name("My New Wardrobe")
                .build();

        wardrobeResponseDto = WardrobeResponseDto.builder()
                .id(1L)
                .name("My New Wardrobe")
                .build();

        user = User.builder()
                .id(1L)
                .username("user1")
                .build();
    }

    @Test
    void createWardrobe_success() throws Exception {
        // Given
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(user);
        when(wardrobeService.createWardrobe(anyLong(), any(WardrobeDto.class))).thenReturn(wardrobeResponseDto);

        // When & Then
        mockMvc.perform(post("/api/wardrobes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wardrobeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Wardrobe created successfully")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is("My New Wardrobe")));

        // Verify service method call
        verify(wardrobeService).createWardrobe(anyLong(), any(WardrobeDto.class));
    }

    @Test
    void getUserWardrobes_success() throws Exception {
        // Given
        List<WardrobeResponseDto> wardrobes = List.of(wardrobeResponseDto);
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(user);
        when(wardrobeService.getUserWardrobes(anyLong())).thenReturn(wardrobes);

        // When & Then
        mockMvc.perform(get("/api/wardrobes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("User wardrobes retrieved successfully")))
                .andExpect(jsonPath("$.data[0].id", is(1)))
                .andExpect(jsonPath("$.data[0].name", is("My New Wardrobe")));

        // Verify service method call
        verify(wardrobeService).getUserWardrobes(anyLong());
    }

    @Test
    void getWardrobeById_success() throws Exception {
        // Given
        Long wardrobeId = 1L;
        when(wardrobeService.getWardrobeById(wardrobeId)).thenReturn(wardrobeResponseDto);

        // When & Then
        mockMvc.perform(get("/api/wardrobes/{id}", wardrobeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Wardrobe retrieved successfully")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is("My New Wardrobe")));

        // Verify service method call
        verify(wardrobeService).getWardrobeById(wardrobeId);
    }

    @Test
    void updateWardrobe_success() throws Exception {
        // Given
        Long wardrobeId = 1L;
        when(wardrobeService.updateWardrobe(eq(wardrobeId), any(WardrobeDto.class))).thenReturn(wardrobeResponseDto);

        // When & Then
        mockMvc.perform(put("/api/wardrobes/{id}", wardrobeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wardrobeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Wardrobe updated successfully")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is("My New Wardrobe")));

        // Verify service method call
        verify(wardrobeService).updateWardrobe(eq(wardrobeId), any(WardrobeDto.class));
    }

    @Test
    void deleteWardrobe_success() throws Exception {
        // Given
        Long wardrobeId = 1L;
        doNothing().when(wardrobeService).deleteWardrobe(wardrobeId);

        // When & Then
        mockMvc.perform(delete("/api/wardrobes/{id}", wardrobeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Wardrobe deleted successfully")));

        // Verify service method call
        verify(wardrobeService).deleteWardrobe(wardrobeId);
    }

    @Test
    void deleteWardrobe_notFound() throws Exception {
        // Given
        Long wardrobeId = 1L;
        doThrow(new ResourceNotFoundException("Wardrobe not found")).when(wardrobeService).deleteWardrobe(wardrobeId);

        // When & Then
        mockMvc.perform(delete("/api/wardrobes/{id}", wardrobeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Wardrobe not found")));

        // Verify service method call
        verify(wardrobeService).deleteWardrobe(wardrobeId);
    }
}
