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

        private void performCreateOrUpdateItemTest(String url, String method, boolean hasImage) throws Exception {
                MockMultipartFile itemPart = new MockMultipartFile(
                        "item",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsString(testItemDto).getBytes());

                var requestBuilder = multipart(url)
                        .file(itemPart);

                if (hasImage) {
                        requestBuilder.file(testImageFile);
                }

                requestBuilder.with(request -> {
                                request.setMethod(method);
                                return request;
                        }).andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.message", containsString("Item")))
                        .andExpect(jsonPath("$.data.id", is(1)))
                        .andExpect(jsonPath("$.data.name", is("Test Item")))
                        .andExpect(jsonPath("$.data.category", is("TOPS")))
                        .andExpect(jsonPath("$.data.size", is("M")));

                verify(itemService).createItem(anyLong(), anyLong(), any(ItemDto.class), any());
        }

        @Test
        void createItem_Success_WithImage() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(itemService.createItem(anyLong(), anyLong(), any(ItemDto.class), any())).thenReturn(testItemResponseDto);

                performCreateOrUpdateItemTest("/api/items", "POST", true);
        }

        @Test
        void createItem_Success_WithoutImage() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(itemService.createItem(anyLong(), anyLong(), any(ItemDto.class), any())).thenReturn(testItemResponseDto);

                performCreateOrUpdateItemTest("/api/items", "POST", false);
        }

        @Test
        void createItem_Failure() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(itemService.createItem(anyLong(), anyLong(), any(ItemDto.class), any()))
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

                verify(itemService).createItem(anyLong(), anyLong(), any(ItemDto.class), any());
        }

        // Refactor other tests similarly by using helper methods like `performCreateOrUpdateItemTest`.

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

        // Refactor remaining tests following the same pattern.
}
