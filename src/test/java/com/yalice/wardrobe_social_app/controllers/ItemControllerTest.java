package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.dtos.item.ItemDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ItemControllerTest {

        private MockMvc mockMvc;

        @Mock
        private ItemService itemService;

        @Mock
        private AuthUtils authUtils;

        @InjectMocks
        private ItemController itemController;

        private final ObjectMapper objectMapper = new ObjectMapper();
        private User testUser;
        private ItemDto testItemDto;
        private ItemResponseDto testItemResponseDto;
        private MockMultipartFile testImageFile;
        private List<ItemResponseDto> testItemList;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders.standaloneSetup(itemController)
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

                testItemDto = ItemDto.builder()
                                .name("Test Item")
                                .brand("Test Brand")
                                .category("TOPS")
                                .size("M")
                                .color("Blue")
                                .build();

                testItemResponseDto = ItemResponseDto.builder()
                                .id(1L)
                                .name("Test Item")
                                .brand("Test Brand")
                                .category("TOPS")
                                .size("M")
                                .color("Blue")
                                .imageUrl("https://example.com/item.jpg")
                                .profileId(1L)
                                .wardrobeId(1L)
                                .build();

                testItemList = Arrays.asList(
                                testItemResponseDto,
                                ItemResponseDto.builder()
                                                .id(2L)
                                                .name("Another Item")
                                                .brand("Another Brand")
                                                .category("BOTTOMS")
                                                .size("L")
                                                .color("Red")
                                                .imageUrl("https://example.com/another-item.jpg")
                                                .profileId(1L)
                                                .wardrobeId(1L)
                                                .build());

                testImageFile = new MockMultipartFile(
                                "image",
                                "test.jpg",
                                MediaType.IMAGE_JPEG_VALUE,
                                "test image content".getBytes());
        }

        @Test
        void createItem_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(itemService.createItem(anyLong(), any(ItemDto.class), any())).thenReturn(testItemResponseDto);

                MockMultipartFile itemPart = new MockMultipartFile(
                                "item",
                                "",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(testItemDto).getBytes());

                mockMvc.perform(multipart("/api/items")
                                .file(itemPart)
                                .file(testImageFile))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Item created successfully")))
                                .andExpect(jsonPath("$.data.id", is(1)))
                                .andExpect(jsonPath("$.data.name", is("Test Item")))
                                .andExpect(jsonPath("$.data.category", is("TOPS")))
                                .andExpect(jsonPath("$.data.size", is("M")));

                verify(itemService).createItem(anyLong(), any(ItemDto.class), any());
        }

        @Test
        void createItem_WithoutImage_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(itemService.createItem(anyLong(), any(ItemDto.class), any())).thenReturn(testItemResponseDto);

                MockMultipartFile itemPart = new MockMultipartFile(
                                "item",
                                "",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(testItemDto).getBytes());

                mockMvc.perform(multipart("/api/items")
                                .file(itemPart))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Item created successfully")))
                                .andExpect(jsonPath("$.data.id", is(1)))
                                .andExpect(jsonPath("$.data.name", is("Test Item")));

                verify(itemService).createItem(anyLong(), any(ItemDto.class), any());
        }

        @Test
        void createItem_Error() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(itemService.createItem(anyLong(), any(ItemDto.class), any()))
                                .thenThrow(new RuntimeException("Failed to create item"));

                MockMultipartFile itemPart = new MockMultipartFile(
                                "item",
                                "",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(testItemDto).getBytes());

                mockMvc.perform(multipart("/api/items")
                                .file(itemPart))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.success", is(false)))
                                .andExpect(jsonPath("$.message", is("Failed to create item")));

                verify(itemService).createItem(anyLong(), any(ItemDto.class), any());
        }

        @Test
        void updateItem_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class), any()))
                                .thenReturn(testItemResponseDto);

                MockMultipartFile itemPart = new MockMultipartFile(
                                "item",
                                "",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(testItemDto).getBytes());

                mockMvc.perform(multipart("/api/items/1")
                                .file(itemPart)
                                .file(testImageFile)
                                .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                }))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Item updated successfully")))
                                .andExpect(jsonPath("$.data.id", is(1)))
                                .andExpect(jsonPath("$.data.name", is("Test Item")))
                                .andExpect(jsonPath("$.data.category", is("TOPS")));

                verify(itemService).updateItem(anyLong(), anyLong(), any(ItemDto.class), any());
        }

        @Test
        void updateItem_WithoutImage_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class), any()))
                                .thenReturn(testItemResponseDto);

                MockMultipartFile itemPart = new MockMultipartFile(
                                "item",
                                "",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(testItemDto).getBytes());

                mockMvc.perform(multipart("/api/items/1")
                                .file(itemPart)
                                .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                }))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Item updated successfully")))
                                .andExpect(jsonPath("$.data.id", is(1)))
                                .andExpect(jsonPath("$.data.name", is("Test Item")));

                verify(itemService).updateItem(anyLong(), anyLong(), any(ItemDto.class), any());
        }

        @Test
        void updateItem_Error() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class), any()))
                                .thenThrow(new RuntimeException("Failed to update item"));

                MockMultipartFile itemPart = new MockMultipartFile(
                                "item",
                                "",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(testItemDto).getBytes());

                mockMvc.perform(multipart("/api/items/1")
                                .file(itemPart)
                                .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                }))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.success", is(false)))
                                .andExpect(jsonPath("$.message", is("Failed to update item")));

                verify(itemService).updateItem(anyLong(), anyLong(), any(ItemDto.class), any());
        }

        @Test
        void deleteItem_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                doNothing().when(itemService).deleteItem(anyLong(), anyLong());

                mockMvc.perform(delete("/api/items/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Item deleted successfully")));

                verify(itemService).deleteItem(anyLong(), anyLong());
        }

        @Test
        void deleteItem_Error() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                doThrow(new RuntimeException("Failed to delete item")).when(itemService).deleteItem(anyLong(),
                                anyLong());

                mockMvc.perform(delete("/api/items/1"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.success", is(false)))
                                .andExpect(jsonPath("$.message", is("Failed to delete item")));

                verify(itemService).deleteItem(anyLong(), anyLong());
        }

        @Test
        void getMyItems_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(itemService.getUserItems(anyLong())).thenReturn(testItemList);

                mockMvc.perform(get("/api/items/my-items"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Items retrieved successfully")))
                                .andExpect(jsonPath("$.data", hasSize(2)))
                                .andExpect(jsonPath("$.data[0].name", is("Test Item")))
                                .andExpect(jsonPath("$.data[1].name", is("Another Item")));

                verify(itemService).getUserItems(anyLong());
        }

        @Test
        void getMyItems_Error() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(itemService.getUserItems(anyLong()))
                                .thenThrow(new RuntimeException("Failed to retrieve items"));

                mockMvc.perform(get("/api/items/my-items"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.success", is(false)))
                                .andExpect(jsonPath("$.message", is("Failed to retrieve items")));

                verify(itemService).getUserItems(anyLong());
        }

        @Test
        void getItem_Success() throws Exception {
                when(itemService.getItem(anyLong())).thenReturn(testItemResponseDto);

                mockMvc.perform(get("/api/items/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Item retrieved successfully")))
                                .andExpect(jsonPath("$.data.id", is(1)))
                                .andExpect(jsonPath("$.data.name", is("Test Item")))
                                .andExpect(jsonPath("$.data.category", is("TOPS")));

                verify(itemService).getItem(anyLong());
        }

        @Test
        void getItem_NotFound() throws Exception {
                when(itemService.getItem(anyLong()))
                                .thenThrow(new RuntimeException("Item not found"));

                mockMvc.perform(get("/api/items/999"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.success", is(false)))
                                .andExpect(jsonPath("$.message", is("Item not found with ID: 999")));

                verify(itemService).getItem(anyLong());
        }

        @Test
        void getItem_Error() throws Exception {
                when(itemService.getItem(anyLong()))
                                .thenThrow(new RuntimeException("Failed to retrieve item"));

                mockMvc.perform(get("/api/items/1"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.success", is(false)))
                                .andExpect(jsonPath("$.message", is("Failed to retrieve item")));

                verify(itemService).getItem(anyLong());
        }
}
