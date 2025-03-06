package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.yalice.wardrobe_social_app.dtos.feed.FeedItemDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.interfaces.FeedService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FeedControllerTest {

        private MockMvc mockMvc;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Mock
        private FeedService feedService;

        @Mock
        private UserSearchService userSearchService;

        @Mock
        private AuthUtils authUtils;

        @InjectMocks
        private FeedController feedController;

        private User testUser;
        private Post testPost;
        private FeedItemDto testFeedItemDto;
        private Page<Post> testPostPage;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders.standaloneSetup(feedController)
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
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);

                UserResponseDto userResponseDto = UserResponseDto.builder()
                                .id(1L)
                                .username("testuser")
                                .email("test@example.com")
                                .build();

                OutfitResponseDto outfitResponseDto = OutfitResponseDto.builder()
                                .id(1L)
                                .name("Test Outfit")
                                .description("A test outfit")
                                .season("SUMMER")
                                .isFavorite(false)
                                .isPublic(true)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .profileId(1L)
                                .build();

                testFeedItemDto = new FeedItemDto();
                testFeedItemDto.setId(1L);
                testFeedItemDto.setType("OUTFIT");
                testFeedItemDto.setOutfit(outfitResponseDto);
                testFeedItemDto.setUser(userResponseDto);
                testFeedItemDto.setCreatedAt(LocalDateTime.now());
                testFeedItemDto.setUpdatedAt(LocalDateTime.now());
                testFeedItemDto.setSeason("SUMMER");
                testFeedItemDto.setCategory("CASUAL");
                testFeedItemDto.setLikesCount(5);
                testFeedItemDto.setCommentsCount(2);
                testFeedItemDto.setLikedByCurrentUser(false);

                testPost = Post.builder()
                                .id(1L)
                                .title("Test Post")
                                .content("This is a test post")
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                testPostPage = new PageImpl<>(Arrays.asList(testPost), PageRequest.of(0, 20), 1);
        }

        @AfterEach
        void tearDown() {
                SecurityContextHolder.clearContext();
        }

        @Test
        void getFeed_Success() throws Exception {
                List<FeedItemDto> feedItems = Arrays.asList(testFeedItemDto);
                when(feedService.getFeed(anyLong(), anyInt(), anyInt())).thenReturn(feedItems);

                mockMvc.perform(get("/api/feed")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Feed retrieved successfully")))
                                .andExpect(jsonPath("$.data", hasSize(1)))
                                .andExpect(jsonPath("$.data[0].id", is(1)))
                                .andExpect(jsonPath("$.data[0].type", is("OUTFIT")))
                                .andExpect(jsonPath("$.data[0].season", is("SUMMER")));

                verify(feedService).getFeed(anyLong(), anyInt(), anyInt());
        }

        @Test
        void getFeedBySeason_Success() throws Exception {
                List<FeedItemDto> feedItems = Arrays.asList(testFeedItemDto);
                when(feedService.getFeedBySeason(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(feedItems);

                mockMvc.perform(get("/api/feed/season/SUMMER")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Feed retrieved successfully")))
                                .andExpect(jsonPath("$.data", hasSize(1)))
                                .andExpect(jsonPath("$.data[0].id", is(1)))
                                .andExpect(jsonPath("$.data[0].type", is("OUTFIT")))
                                .andExpect(jsonPath("$.data[0].season", is("SUMMER")));

                verify(feedService).getFeedBySeason(anyLong(), anyString(), anyInt(), anyInt());
        }

        @Test
        void getFeedByCategory_Success() throws Exception {
                List<FeedItemDto> feedItems = Arrays.asList(testFeedItemDto);
                when(feedService.getFeedByCategory(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(feedItems);

                mockMvc.perform(get("/api/feed/category/CASUAL")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Feed retrieved successfully")))
                                .andExpect(jsonPath("$.data", hasSize(1)))
                                .andExpect(jsonPath("$.data[0].id", is(1)))
                                .andExpect(jsonPath("$.data[0].type", is("OUTFIT")))
                                .andExpect(jsonPath("$.data[0].category", is("CASUAL")));

                verify(feedService).getFeedByCategory(anyLong(), anyString(), anyInt(), anyInt());
        }

        @Test
        void getUserPosts_Success() throws Exception {
                when(feedService.getUserPosts(anyLong(), anyLong(), eq(PageRequest.of(0, 20))))
                                .thenReturn(testPostPage);

                mockMvc.perform(get("/api/feed/users/1/posts")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("User posts retrieved successfully")))
                                .andExpect(jsonPath("$.data.content", hasSize(1)))
                                .andExpect(jsonPath("$.data.content[0].id", is(1)))
                                .andExpect(jsonPath("$.data.content[0].title", is("Test Post")));

                verify(feedService).getUserPosts(anyLong(), anyLong(), eq(PageRequest.of(0, 20)));
        }

        @Test
        void getFeed_Error() throws Exception {
                when(feedService.getFeed(anyLong(), anyInt(), anyInt()))
                                .thenThrow(new RuntimeException("Database error"));

                mockMvc.perform(get("/api/feed")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.success", is(false)))
                                .andExpect(jsonPath("$.message", is("Failed to retrieve feed")));

                verify(feedService).getFeed(anyLong(), anyInt(), anyInt());
        }

        @Test
        void getFeedBySeason_Error() throws Exception {
                when(feedService.getFeedBySeason(anyLong(), anyString(), anyInt(), anyInt()))
                                .thenThrow(new RuntimeException("Database error"));

                mockMvc.perform(get("/api/feed/season/SUMMER")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.success", is(false)))
                                .andExpect(jsonPath("$.message", is("Failed to retrieve feed")));

                verify(feedService).getFeedBySeason(anyLong(), anyString(), anyInt(), anyInt());
        }

        @Test
        void getFeedByCategory_Error() throws Exception {
                when(feedService.getFeedByCategory(anyLong(), anyString(), anyInt(), anyInt()))
                                .thenThrow(new RuntimeException("Database error"));

                mockMvc.perform(get("/api/feed/category/CASUAL")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.success", is(false)))
                                .andExpect(jsonPath("$.message", is("Failed to retrieve feed")));

                verify(feedService).getFeedByCategory(anyLong(), anyString(), anyInt(), anyInt());
        }
}
