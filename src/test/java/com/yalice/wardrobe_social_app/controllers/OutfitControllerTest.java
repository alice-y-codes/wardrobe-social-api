//package com.yalice.wardrobe_social_app.controllers;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
//import com.yalice.wardrobe_social_app.dtos.outfit.OutfitDto;
//import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
//import com.yalice.wardrobe_social_app.entities.User;
//import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
//import com.yalice.wardrobe_social_app.interfaces.OutfitService;
//import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import static org.hamcrest.Matchers.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//class OutfitControllerTest {
//
//        private MockMvc mockMvc;
//
//        @Mock
//        private OutfitService outfitService;
//
//        @Mock
//        private AuthUtils authUtils;
//
//        @InjectMocks
//        private OutfitController outfitController;
//
//        private final ObjectMapper objectMapper = new ObjectMapper();
//        private User testUser;
//        private OutfitDto testOutfitDto;
//        private OutfitResponseDto testOutfitResponseDto;
//        private MockMultipartFile testImageFile;
//        private Set<ItemResponseDto> testItems;
//        private List<OutfitResponseDto> testOutfitList;
//
//        @BeforeEach
//        void setUp() {
//                MockitoAnnotations.openMocks(this);
//                mockMvc = MockMvcBuilders.standaloneSetup(outfitController)
//                        .setControllerAdvice(new GlobalExceptionHandler())
//                        .build();
//
//                initializeTestData();
//        }
//
//        private void initializeTestData() {
//                testUser = User.builder()
//                        .id(1L)
//                        .username("testuser")
//                        .email("test@example.com")
//                        .build();
//
//                testItems = new HashSet<>();
//                testItems.add(ItemResponseDto.builder()
//                        .id(1L)
//                        .name("Test Item")
//                        .brand("Test Brand")
//                        .category("TOPS")
//                        .size("M")
//                        .color("Blue")
//                        .imageUrl("https://example.com/test.jpg")
//                        .profileId(1L)
//                        .wardrobeId(1L)
//                        .build());
//
//                testOutfitDto = OutfitDto.builder()
//                        .name("Test Outfit")
//                        .description("A test outfit")
//                        .season("SUMMER")
//                        .isFavorite(false)
//                        .isPublic(true)
//                        .itemIds(new HashSet<>(Arrays.asList(1L)))
//                        .userId(1L)
//                        .build();
//
//                testOutfitResponseDto = OutfitResponseDto.builder()
//                        .id(1L)
//                        .name("Test Outfit")
//                        .description("A test outfit")
//                        .season("SUMMER")
//                        .isFavorite(false)
//                        .isPublic(true)
//                        .createdAt(LocalDateTime.now())
//                        .updatedAt(LocalDateTime.now())
//                        .items(testItems)
//                        .profileId(1L)
//                        .build();
//
//                testOutfitList = Arrays.asList(
//                        testOutfitResponseDto,
//                        OutfitResponseDto.builder()
//                                .id(2L)
//                                .name("Another Outfit")
//                                .description("Another test outfit")
//                                .season("WINTER")
//                                .isFavorite(true)
//                                .isPublic(false)
//                                .profileId(1L)
//                                .build());
//
//                testImageFile = new MockMultipartFile(
//                        "image",
//                        "test.jpg",
//                        MediaType.IMAGE_JPEG_VALUE,
//                        "test image content".getBytes());
//        }
//
//        @Test
//        void createOutfit_Success() throws Exception {
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//                when(outfitService.createOutfit(anyLong(), any(OutfitDto.class), any()))
//                        .thenReturn(testOutfitResponseDto);
//
//                MockMultipartFile outfitPart = new MockMultipartFile(
//                        "outfit",
//                        "",
//                        MediaType.APPLICATION_JSON_VALUE,
//                        objectMapper.writeValueAsString(testOutfitDto).getBytes());
//
//                mockMvc.perform(multipart("/api/outfits")
//                                .file(outfitPart)
//                                .file(testImageFile))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.success", is(true)))
//                        .andExpect(jsonPath("$.message", is("Outfit created successfully")))
//                        .andExpect(jsonPath("$.data.id", is(1)))
//                        .andExpect(jsonPath("$.data.name", is("Test Outfit")))
//                        .andExpect(jsonPath("$.data.season", is("SUMMER")));
//
//                verify(outfitService).createOutfit(anyLong(), any(OutfitDto.class), any());
//        }
//
//        @Test
//        void createOutfit_WithoutImage_Success() throws Exception {
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//                when(outfitService.createOutfit(anyLong(), any(OutfitDto.class), any()))
//                        .thenReturn(testOutfitResponseDto);
//
//                MockMultipartFile outfitPart = new MockMultipartFile(
//                        "outfit",
//                        "",
//                        MediaType.APPLICATION_JSON_VALUE,
//                        objectMapper.writeValueAsString(testOutfitDto).getBytes());
//
//                mockMvc.perform(multipart("/api/outfits")
//                                .file(outfitPart))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.success", is(true)))
//                        .andExpect(jsonPath("$.message", is("Outfit created successfully")))
//                        .andExpect(jsonPath("$.data.id", is(1)))
//                        .andExpect(jsonPath("$.data.name", is("Test Outfit")));
//
//                verify(outfitService).createOutfit(anyLong(), any(OutfitDto.class), any());
//        }
//
//        @Test
//        void createOutfit_Error() throws Exception {
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//                when(outfitService.createOutfit(anyLong(), any(OutfitDto.class), any()))
//                        .thenThrow(new RuntimeException("Failed to create outfit"));
//
//                MockMultipartFile outfitPart = new MockMultipartFile(
//                        "outfit",
//                        "",
//                        MediaType.APPLICATION_JSON_VALUE,
//                        objectMapper.writeValueAsString(testOutfitDto).getBytes());
//
//                mockMvc.perform(multipart("/api/outfits")
//                                .file(outfitPart))
//                        .andExpect(status().isInternalServerError())
//                        .andExpect(jsonPath("$.success", is(false)))
//                        .andExpect(jsonPath("$.message", is("Failed to create outfit")));
//
//                verify(outfitService).createOutfit(anyLong(), any(OutfitDto.class), any());
//        }
//
//        @Test
//        void updateOutfit_Success() throws Exception {
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//                when(outfitService.updateOutfit(anyLong(), anyLong(), any(OutfitDto.class), any()))
//                        .thenReturn(testOutfitResponseDto);
//
//                MockMultipartFile outfitPart = new MockMultipartFile(
//                        "outfit",
//                        "",
//                        MediaType.APPLICATION_JSON_VALUE,
//                        objectMapper.writeValueAsString(testOutfitDto).getBytes());
//
//                mockMvc.perform(multipart("/api/outfits/1")
//                                .file(outfitPart)
//                                .file(testImageFile)
//                                .with(request -> {
//                                        request.setMethod("PUT");
//                                        return request;
//                                }))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.success", is(true)))
//                        .andExpect(jsonPath("$.message", is("Outfit updated successfully")))
//                        .andExpect(jsonPath("$.data.id", is(1)))
//                        .andExpect(jsonPath("$.data.name", is("Test Outfit")));
//
//                verify(outfitService).updateOutfit(anyLong(), anyLong(), any(OutfitDto.class), any());
//        }
//
//        @Test
//        void updateOutfit_Error() throws Exception {
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//                when(outfitService.updateOutfit(anyLong(), anyLong(), any(OutfitDto.class), any()))
//                        .thenThrow(new RuntimeException("Failed to update outfit"));
//
//                MockMultipartFile outfitPart = new MockMultipartFile(
//                        "outfit",
//                        "",
//                        MediaType.APPLICATION_JSON_VALUE,
//                        objectMapper.writeValueAsString(testOutfitDto).getBytes());
//
//                mockMvc.perform(multipart("/api/outfits/1")
//                                .file(outfitPart)
//                                .with(request -> {
//                                        request.setMethod("PUT");
//                                        return request;
//                                }))
//                        .andExpect(status().isInternalServerError())
//                        .andExpect(jsonPath("$.success", is(false)))
//                        .andExpect(jsonPath("$.message", is("Failed to update outfit")));
//
//                verify(outfitService).updateOutfit(anyLong(), anyLong(), any(OutfitDto.class), any());
//        }
//
//        @Test
//        void deleteOutfit_Success() throws Exception {
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//                doNothing().when(outfitService).deleteOutfit(anyLong(), anyLong());
//
//                mockMvc.perform(delete("/api/outfits/1"))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.success", is(true)))
//                        .andExpect(jsonPath("$.message", is("Outfit deleted successfully")));
//
//                verify(outfitService).deleteOutfit(anyLong(), anyLong());
//        }
//
//        @Test
//        void deleteOutfit_Error() throws Exception {
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//                doThrow(new RuntimeException("Failed to delete outfit")).when(outfitService).deleteOutfit(anyLong(),
//                        anyLong());
//
//                mockMvc.perform(delete("/api/outfits/1"))
//                        .andExpect(status().isInternalServerError())
//                        .andExpect(jsonPath("$.success", is(false)))
//                        .andExpect(jsonPath("$.message", is("Failed to delete outfit")));
//
//                verify(outfitService).deleteOutfit(anyLong(), anyLong());
//        }
//
//        @Test
//        void getMyOutfits_Success() throws Exception {
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//                when(outfitService.getUserOutfits(anyLong())).thenReturn(testOutfitList);
//
//                mockMvc.perform(get("/api/outfits/my-outfits"))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.success", is(true)))
//                        .andExpect(jsonPath("$.message", is("Outfits retrieved successfully")))
//                        .andExpect(jsonPath("$.data", hasSize(2)))
//                        .andExpect(jsonPath("$.data[0].name", is("Test Outfit")))
//                        .andExpect(jsonPath("$.data[1].name", is("Another Outfit")));
//
//                verify(outfitService).getUserOutfits(anyLong());
//        }
//
//        @Test
//        void getMyOutfits_Error() throws Exception {
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//                when(outfitService.getUserOutfits(anyLong()))
//                        .thenThrow(new RuntimeException("Failed to retrieve outfits"));
//
//                mockMvc.perform(get("/api/outfits/my-outfits"))
//                        .andExpect(status().isInternalServerError())
//                        .andExpect(jsonPath("$.success", is(false)))
//                        .andExpect(jsonPath("$.message", is("Failed to retrieve outfits")));
//
//                verify(outfitService).getUserOutfits(anyLong());
//        }
//
//        @Test
//        void getOutfit_Success() throws Exception {
//                when(outfitService.getOutfit(anyLong())).thenReturn(testOutfitResponseDto);
//
//                mockMvc.perform(get("/api/outfits/1"))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.success", is(true)))
//                        .andExpect(jsonPath("$.message", is("Outfit retrieved successfully")))
//                        .andExpect(jsonPath("$.data.id", is(1)))
//                        .andExpect(jsonPath("$.data.name", is("Test Outfit")))
//                        .andExpect(jsonPath("$.data.season", is("SUMMER")));
//
//                verify(outfitService).getOutfit(anyLong());
//        }
//
//        @Test
//        void getOutfit_NotFound() throws Exception {
//                when(outfitService.getOutfit(anyLong()))
//                        .thenThrow(new RuntimeException("Outfit not found"));
//
//                mockMvc.perform(get("/api/outfits/999"))
//                        .andExpect(status().isNotFound())
//                        .andExpect(jsonPath("$.success", is(false)))
//                        .andExpect(jsonPath("$.message", is("Outfit not found with ID: 999")));
//
//                verify(outfitService).getOutfit(anyLong());
//        }
//
//        @Test
//        void getUserOutfits_Success() throws Exception {
//                when(outfitService.getUserOutfits(anyLong())).thenReturn(testOutfitList);
//
//                mockMvc.perform(get("/api/outfits/users/1"))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.success", is(true)))
//                        .andExpect(jsonPath("$.message", is("User outfits retrieved successfully")))
//                        .andExpect(jsonPath("$.data", hasSize(2)))
//                        .andExpect(jsonPath("$.data[0].name", is("Test Outfit")))
//                        .andExpect(jsonPath("$.data[1].name", is("Another Outfit")));
//
//                verify(outfitService).getUserOutfits(anyLong());
//        }
//
//        @Test
//        void getUserOutfits_Error() throws Exception {
//                when(outfitService.getUserOutfits(anyLong()))
//                        .thenThrow(new RuntimeException("Failed to retrieve outfits"));
//
//                mockMvc.perform(get("/api/outfits/users/1"))
//                        .andExpect(status().isInternalServerError())
//                        .andExpect(jsonPath("$.success", is(false)))
//                        .andExpect(jsonPath("$.message", is("Failed to retrieve outfits")));
//
//                verify(outfitService).getUserOutfits(anyLong());
//        }
//
//        @Test
//        void addItemToOutfit_Success() throws Exception {
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//                when(outfitService.getOutfit(anyLong())).thenReturn(testOutfitResponseDto);
//                when(outfitService.addItemToOutfit(anyLong(), anyLong())).thenReturn(testOutfitResponseDto);
//
//                mockMvc.perform(post("/api/outfits/1/items/1"))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.success", is(true)))
//                        .andExpect(jsonPath("$.message", is("Item added to outfit successfully")))
//                        .andExpect(jsonPath("$.data.id", is(1)))
//                        .andExpect(jsonPath("$.data.name", is("Test Outfit")));
//
//                verify(outfitService).addItemToOutfit(anyLong(), anyLong());
//        }
//
//        @Test
//        void addItemToOutfit_Unauthorized() throws Exception {
//                User unauthorizedUser = User.builder()
//                        .id(2L)
//                        .username("otheruser")
//                        .build();
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(unauthorizedUser);
//                when(outfitService.getOutfit(anyLong())).thenReturn(testOutfitResponseDto);
//
//                mockMvc.perform(post("/api/outfits/1/items/1"))
//                        .andExpect(status().isForbidden())
//                        .andExpect(jsonPath("$.success", is(false)))
//                        .andExpect(jsonPath("$.message",
//                                is("You don't have permission to modify this outfit")));
//
//                verify(outfitService, never()).addItemToOutfit(anyLong(), anyLong());
//        }
//
//        @Test
//        void removeItemFromOutfit_Success() throws Exception {
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//                when(outfitService.getOutfit(anyLong())).thenReturn(testOutfitResponseDto);
//                when(outfitService.removeItemFromOutfit(anyLong(), anyLong())).thenReturn(testOutfitResponseDto);
//
//                mockMvc.perform(delete("/api/outfits/1/items/1"))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.success", is(true)))
//                        .andExpect(jsonPath("$.message", is("Item removed from outfit successfully")))
//                        .andExpect(jsonPath("$.data.id", is(1)))
//                        .andExpect(jsonPath("$.data.name", is("Test Outfit")));
//
//                verify(outfitService).removeItemFromOutfit(anyLong(), anyLong());
//        }
//
//        @Test
//        void removeItemFromOutfit_Unauthorized() throws Exception {
//                User unauthorizedUser = User.builder()
//                        .id(2L)
//                        .username("otheruser")
//                        .build();
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(unauthorizedUser);
//                when(outfitService.getOutfit(anyLong())).thenReturn(testOutfitResponseDto);
//
//                mockMvc.perform(delete("/api/outfits/1/items/1"))
//                        .andExpect(status().isForbidden())
//                        .andExpect(jsonPath("$.success", is(false)))
//                        .andExpect(jsonPath("$.message",
//                                is("You don't have permission to modify this outfit")));
//
//                verify(outfitService, never()).removeItemFromOutfit(anyLong(), anyLong());
//        }
//
//        @Test
//        void getOutfitsBySeason_Success() throws Exception {
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//                when(outfitService.getUserOutfits(anyLong())).thenReturn(testOutfitList);
//                when(outfitService.getOutfit(anyLong())).thenReturn(testOutfitResponseDto);
//
//                mockMvc.perform(get("/api/outfits/season/SUMMER"))
//                        .andExpect(status().isOk())
//                        .andExpect(jsonPath("$.success", is(true)))
//                        .andExpect(jsonPath("$.message", is("Seasonal outfits retrieved successfully")))
//                        .andExpect(jsonPath("$.data", hasSize(1)))
//                        .andExpect(jsonPath("$.data[0].season", is("SUMMER")));
//
//                verify(outfitService).getUserOutfits(anyLong());
//        }
//
//        @Test
//        void getOutfitsBySeason_Error() throws Exception {
//                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//                when(outfitService.getUserOutfits(anyLong()))
//                        .thenThrow(new RuntimeException("Failed to retrieve outfits"));
//
//                mockMvc.perform(get("/api/outfits/season/SUMMER"))
//                        .andExpect(status().isInternalServerError())
//                        .andExpect(jsonPath("$.success", is(false)))
//                        .andExpect(jsonPath("$.message", is("Failed to retrieve outfits")));
//
//                verify(outfitService).getUserOutfits(anyLong());
//        }
//}
