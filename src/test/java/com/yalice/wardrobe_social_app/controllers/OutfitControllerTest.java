package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OutfitControllerTest {

    @Mock
    private OutfitService outfitService;

    @Mock
    private AuthUtils authUtils;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;

    @InjectMocks
    private OutfitController outfitController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(outfitController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        testUser = User.builder().id(1L).build();
    }

    @Test
    void createOutfit() throws Exception {
        OutfitDto outfitDto = new OutfitDto();
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[1]);
        MockMultipartFile outfitDtoFile = new MockMultipartFile(
                "outfit",
                "",
                "application/json",
                objectMapper.writeValueAsString(outfitDto).getBytes());

        OutfitResponseDto responseDto = new OutfitResponseDto();
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(outfitService.createOutfit(any(), any(), any())).thenReturn(responseDto);

        mockMvc.perform(multipart("/api/outfits")
                .file(image)
                .file(outfitDtoFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void createOutfit_Unauthorized() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow())
                .thenThrow(new SecurityException("Unauthorized"));

        MockMultipartFile outfitJson = new MockMultipartFile(
                "outfit",
                "",
                "application/json",
                objectMapper.writeValueAsString(new OutfitDto()).getBytes());

        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", new byte[1]);

        mockMvc.perform(multipart("/api/outfits")
                .file(imageFile)
                .file(outfitJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateOutfit() throws Exception {
        Long outfitId = 1L;
        OutfitDto outfitDto = new OutfitDto();
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[1]);
        MockMultipartFile outfitDtoFile = new MockMultipartFile(
                "outfit",
                "",
                "application/json",
                objectMapper.writeValueAsString(outfitDto).getBytes());

        OutfitResponseDto responseDto = new OutfitResponseDto();
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(outfitService.updateOutfit(any(), any(), any(), any())).thenReturn(responseDto);

        mockMvc.perform(multipart(HttpMethod.PATCH, "/api/outfits/{outfitId}", outfitId)
                .file(image)
                .file(outfitDtoFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void updateOutfit_NotFound() throws Exception {
        Long outfitId = 1L;
        OutfitDto outfitDto = new OutfitDto();
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[1]);
        MockMultipartFile outfitDtoFile = new MockMultipartFile(
                "outfit",
                "",
                "application/json",
                objectMapper.writeValueAsString(outfitDto).getBytes());

        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(outfitService.updateOutfit(any(), any(), any(), any()))
                .thenThrow(new ResourceNotFoundException("Outfit not found"));

        mockMvc.perform(multipart(HttpMethod.PATCH, "/api/outfits/{outfitId}", outfitId)
                .file(image)
                .file(outfitDtoFile))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Outfit not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void deleteOutfit() throws Exception {
        Long outfitId = 1L;
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        doNothing().when(outfitService).deleteOutfit(any(), any());

        mockMvc.perform(delete("/api/outfits/{outfitId}", outfitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteOutfit_NotFound() throws Exception {
        Long outfitId = 1L;
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        doThrow(new ResourceNotFoundException("Outfit not found"))
                .when(outfitService).deleteOutfit(any(), any());

        mockMvc.perform(delete("/api/outfits/{outfitId}", outfitId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Outfit not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getMyOutfits() throws Exception {
        OutfitResponseDto responseDto = new OutfitResponseDto();
        List<OutfitResponseDto> outfits = Collections.singletonList(responseDto);

        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(outfitService.getUserOutfits(any())).thenReturn(outfits);

        mockMvc.perform(get("/api/outfits/my-outfits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0]").exists());
    }

    @Test
    void getMyOutfits_Unauthorized() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow())
                .thenThrow(new SecurityException("Unauthorized"));

        mockMvc.perform(get("/api/outfits/my-outfits"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getOutfit() throws Exception {
        Long outfitId = 1L;
        OutfitResponseDto responseDto = new OutfitResponseDto();

        when(outfitService.getOutfit(any())).thenReturn(responseDto);

        mockMvc.perform(get("/api/outfits/{outfitId}", outfitId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void getOutfit_NotFound() throws Exception {
        Long outfitId = 1L;
        when(outfitService.getOutfit(any()))
                .thenThrow(new ResourceNotFoundException("Outfit not found"));

        mockMvc.perform(get("/api/outfits/{outfitId}", outfitId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Outfit not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void addItemToOutfit() throws Exception {
        Long outfitId = 1L;
        Long itemId = 1L;
        OutfitResponseDto responseDto = new OutfitResponseDto();

        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(outfitService.addItemToOutfit(any(), any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/outfits/{outfitId}/items/{itemId}", outfitId, itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void addItemToOutfit_NotFound() throws Exception {
        Long outfitId = 1L;
        Long itemId = 1L;

        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(outfitService.addItemToOutfit(any(), any()))
                .thenThrow(new ResourceNotFoundException("Outfit not found"));

        mockMvc.perform(post("/api/outfits/{outfitId}/items/{itemId}", outfitId, itemId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Outfit not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void removeItemFromOutfit() throws Exception {
        Long outfitId = 1L;
        Long itemId = 1L;
        OutfitResponseDto responseDto = new OutfitResponseDto();

        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(outfitService.removeItemFromOutfit(any(), any())).thenReturn(responseDto);

        mockMvc.perform(delete("/api/outfits/{outfitId}/items/{itemId}", outfitId, itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void removeItemFromOutfit_NotFound() throws Exception {
        Long outfitId = 1L;
        Long itemId = 1L;

        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(outfitService.removeItemFromOutfit(any(), any()))
                .thenThrow(new ResourceNotFoundException("Outfit not found"));

        mockMvc.perform(delete("/api/outfits/{outfitId}/items/{itemId}", outfitId, itemId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Outfit not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getMyOutfits_EmptyList() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(outfitService.getUserOutfits(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/outfits/my-outfits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
