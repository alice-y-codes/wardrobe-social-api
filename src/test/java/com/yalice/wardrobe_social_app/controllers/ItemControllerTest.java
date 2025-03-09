package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.dtos.item.ItemDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
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

class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @Mock
    private AuthUtils authUtils;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        testUser = User.builder().id(1L).build();
    }

    @Test
    void createItem() throws Exception {
        Long wardrobeId = 1L;
        ItemDto itemDto = new ItemDto(); // Populate with test data
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[1]);
        MockMultipartFile itemDtoFile = new MockMultipartFile(
                "item",
                "",
                "application/json",
                objectMapper.writeValueAsString(itemDto).getBytes());

        ItemResponseDto responseDto = new ItemResponseDto(); // Populate with test data
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(itemService.createItem(any(), any(), any(), any())).thenReturn(responseDto);

        mockMvc.perform(multipart("/api/items/{wardrobeId}", wardrobeId)
                .file(image)
                .file(itemDtoFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void createItem_Unauthorized() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow())
                .thenThrow(new SecurityException("Unauthorized"));

        // Correctly formatted JSON request part
        MockMultipartFile itemJson = new MockMultipartFile(
                "item",
                "",
                "application/json",
                objectMapper.writeValueAsString(new ItemDto()).getBytes() // Empty object or test data
        );

        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", new byte[1]
        );

        mockMvc.perform(multipart("/api/items/{wardrobeId}", 1L)
                        .file(imageFile)
                        .file(itemJson)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized()) // 401
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateItem() throws Exception {
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto(); // Populate with test data
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[1]);
        MockMultipartFile itemDtoFile = new MockMultipartFile(
                "item",
                "",
                "application/json",
                objectMapper.writeValueAsString(itemDto).getBytes());

        ItemResponseDto responseDto = new ItemResponseDto(); // Populate with test data
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(itemService.updateItem(any(), any(), any(), any())).thenReturn(responseDto);

        mockMvc.perform(multipart(HttpMethod.PATCH, "/api/items/{itemId}", itemId)
                .file(image)
                .file(itemDtoFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void updateItem_NotFound() throws Exception {
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto();
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[1]);
        MockMultipartFile itemDtoFile = new MockMultipartFile(
                "item",
                "",
                "application/json",
                objectMapper.writeValueAsString(itemDto).getBytes());

        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(itemService.updateItem(any(), any(), any(), any()))
                .thenThrow(new ResourceNotFoundException("Item not found"));

        mockMvc.perform(multipart(HttpMethod.PATCH, "/api/items/{itemId}", itemId)
                .file(image)
                .file(itemDtoFile))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Item not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void deleteItem() throws Exception {
        Long itemId = 1L;
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        doNothing().when(itemService).deleteItem(any(), any());

        mockMvc.perform(delete("/api/items/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteItem_NotFound() throws Exception {
        Long itemId = 1L;
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        doThrow(new ResourceNotFoundException("Item not found"))
                .when(itemService).deleteItem(any(), any());

        mockMvc.perform(delete("/api/items/{itemId}", itemId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Item not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getMyItems() throws Exception {
        ItemResponseDto responseDto = new ItemResponseDto();
        List<ItemResponseDto> items = Collections.singletonList(responseDto);

        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(itemService.getUserItems(any())).thenReturn(items);

        mockMvc.perform(get("/api/items/my-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0]").exists());
    }

    @Test
    void getMyItems_Unauthorized() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow())
                .thenThrow(new SecurityException("Unauthorized"));

        mockMvc.perform(get("/api/items/my-items"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getItem() throws Exception {
        Long itemId = 1L;
        ItemResponseDto responseDto = new ItemResponseDto();

        when(itemService.getItem(any())).thenReturn(responseDto);

        mockMvc.perform(get("/api/items/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void getItem_NotFound() throws Exception {
        Long itemId = 1L;
        when(itemService.getItem(any()))
                .thenThrow(new ResourceNotFoundException("Item not found"));

        mockMvc.perform(get("/api/items/{itemId}", itemId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Item not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void createItem_MissingImage() throws Exception {
        Long wardrobeId = 1L;
        MockMultipartFile itemDtoFile = new MockMultipartFile(
                "item",
                "",
                "application/json",
                objectMapper.writeValueAsString(new ItemDto()).getBytes());

        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);

        mockMvc.perform(multipart("/api/items/{wardrobeId}", wardrobeId)
                        .file(itemDtoFile))
                .andExpect(status().isBadRequest()) // Expecting failure due to missing image
//                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void createItem_InvalidJson() throws Exception {
        Long wardrobeId = 1L;
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[1]);
        MockMultipartFile invalidJson = new MockMultipartFile(
                "item",
                "",
                "application/json",
                "invalid json".getBytes());

        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);

        mockMvc.perform(multipart("/api/items/{wardrobeId}", wardrobeId)
                        .file(image)
                        .file(invalidJson))
                .andExpect(status().isBadRequest()) // Expecting failure due to malformed JSON
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateItem_NoChanges() throws Exception {
        Long itemId = 1L;
        ItemDto itemDto = new ItemDto(); // Empty or same data
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[1]);
        MockMultipartFile itemDtoFile = new MockMultipartFile(
                "item",
                "",
                "application/json",
                objectMapper.writeValueAsString(itemDto).getBytes());

        ItemResponseDto responseDto = new ItemResponseDto();
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(itemService.updateItem(any(), any(), any(), any())).thenReturn(responseDto);

        mockMvc.perform(multipart(HttpMethod.PATCH, "/api/items/{itemId}", itemId)
                        .file(image)
                        .file(itemDtoFile))
                .andExpect(status().isOk()) // Should succeed even if no changes
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void deleteItem_Unauthorized() throws Exception {
        Long itemId = 1L;
        when(authUtils.getCurrentUserOrElseThrow())
                .thenThrow(new SecurityException("Unauthorized"));

        mockMvc.perform(delete("/api/items/{itemId}", itemId))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getMyItems_EmptyList() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(itemService.getUserItems(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/items/my-items"))
                .andExpect(status().isOk()) // Should return 200 even if empty
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

}
