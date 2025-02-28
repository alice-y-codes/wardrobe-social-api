package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OutfitControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OutfitService outfitService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private OutfitController outfitController;

    private ObjectMapper objectMapper = new ObjectMapper();
    private User testUser;
    private Outfit testOutfit;
    private Item testItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(outfitController).build();

        // Setup security context mock
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("testuser");
        when(authentication.isAuthenticated()).thenReturn(true);

        // Setup test user
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();
        when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Setup test item
        testItem = Item.builder()
                .id(1L)
                .name("Test Item")
                .category("Tops")
                .userId(1L)
                .imageUrl("http://example.com/image.jpg")
                .build();

        // Setup test outfit
        Set<Item> items = new HashSet<>();
        items.add(testItem);

        testOutfit = Outfit.builder()
                .id(1L)
                .name("Test Outfit")
                .description("A test outfit")
                .occasion("Casual")
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .items(items)
                .build();
    }

    @Test
    void createOutfit_Success() throws Exception {
        Outfit outfitToCreate = Outfit.builder()
                .name("New Outfit")
                .description("A new outfit")
                .occasion("Formal")
                .build();

        when(outfitService.createOutfit(eq(1L), any(Outfit.class))).thenReturn(Optional.of(testOutfit));

        mockMvc.perform(post("/api/outfits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(outfitToCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Outfit")))
                .andExpect(jsonPath("$.description", is("A test outfit")))
                .andExpect(jsonPath("$.occasion", is("Casual")));

        verify(outfitService).createOutfit(eq(1L), any(Outfit.class));
    }

    @Test
    void getMyOutfits_Success() throws Exception {
        List<Outfit> outfits = Collections.singletonList(testOutfit);
        when(outfitService.getAllOutfits(1L)).thenReturn(outfits);

        mockMvc.perform(get("/api/outfits/my-outfits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Outfit")));

        verify(outfitService).getAllOutfits(1L);
    }

    @Test
    void getUserOutfits_Success() throws Exception {
        List<Outfit> outfits = Collections.singletonList(testOutfit);
        when(outfitService.getAllOutfits(1L)).thenReturn(outfits);

        mockMvc.perform(get("/api/outfits/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Outfit")));

        verify(outfitService).getAllOutfits(1L);
    }

    @Test
    void getOutfitById_Success() throws Exception {
        when(outfitService.getOutfit(1L)).thenReturn(Optional.of(testOutfit));

        mockMvc.perform(get("/api/outfits/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Outfit")));

        verify(outfitService).getOutfit(1L);
    }

    @Test
    void getOutfitById_NotFound() throws Exception {
        when(outfitService.getOutfit(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/outfits/99"))
                .andExpect(status().isNotFound());

        verify(outfitService).getOutfit(99L);
    }

    @Test
    void updateOutfit_Success() throws Exception {
        Outfit outfitToUpdate = Outfit.builder()
                .name("Updated Outfit")
                .description("An updated outfit")
                .occasion("Business")
                .build();

        when(outfitService.getOutfit(1L)).thenReturn(Optional.of(testOutfit));
        when(outfitService.updateOutfit(eq(1L), any(Outfit.class))).thenReturn(testOutfit);

        mockMvc.perform(put("/api/outfits/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(outfitToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Outfit")));

        verify(outfitService).updateOutfit(eq(1L), any(Outfit.class));
    }

    @Test
    void updateOutfit_NotFound() throws Exception {
        Outfit outfitToUpdate = Outfit.builder()
                .name("Updated Outfit")
                .build();

        when(outfitService.getOutfit(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/outfits/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(outfitToUpdate)))
                .andExpect(status().isNotFound());

        verify(outfitService, never()).updateOutfit(anyLong(), any(Outfit.class));
    }

    @Test
    void deleteOutfit_Success() throws Exception {
        when(outfitService.getOutfit(1L)).thenReturn(Optional.of(testOutfit));
        doNothing().when(outfitService).deleteOutfit(1L);

        mockMvc.perform(delete("/api/outfits/1"))
                .andExpect(status().isNoContent());

        verify(outfitService).deleteOutfit(1L);
    }

    @Test
    void addItemToOutfit_Success() throws Exception {
        when(outfitService.getOutfit(1L)).thenReturn(Optional.of(testOutfit));
        when(outfitService.addItemToOutfit(1L, 2L)).thenReturn(Optional.of(testOutfit));

        mockMvc.perform(post("/api/outfits/1/items/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Outfit")));

        verify(outfitService).addItemToOutfit(1L, 2L);
    }

    @Test
    void removeItemFromOutfit_Success() throws Exception {
        when(outfitService.getOutfit(1L)).thenReturn(Optional.of(testOutfit));
        when(outfitService.removeItemFromOutfit(1L, 1L)).thenReturn(Optional.of(testOutfit));

        mockMvc.perform(delete("/api/outfits/1/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Outfit")));

        verify(outfitService).removeItemFromOutfit(1L, 1L);
    }

    @Test
    void getOutfitsByOccasion_Success() throws Exception {
        List<Outfit> outfits = Collections.singletonList(testOutfit);
        when(outfitService.getOutfitsByOccasion(1L, "Casual")).thenReturn(outfits);

        mockMvc.perform(get("/api/outfits/occasion/Casual"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Outfit")))
                .andExpect(jsonPath("$[0].occasion", is("Casual")));

        verify(outfitService).getOutfitsByOccasion(1L, "Casual");
    }
}
