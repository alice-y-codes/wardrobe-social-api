package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.dtos.feed.FeedItemResponseDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.FeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FeedControllerTest {

        @Mock
        private FeedService feedService;

        @Mock
        private AuthUtils authUtils;

        private MockMvc mockMvc;
        private ObjectMapper objectMapper;
        private User testUser;

        @InjectMocks
        private FeedController feedController;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders
                                .standaloneSetup(feedController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                                .build();
                objectMapper = new ObjectMapper();
                testUser = User.builder()
                                .id(1L)
                                .profile(Profile.builder().id(1L).build())
                                .build();
        }

        @Test
        void getFeed() throws Exception {
                List<FeedItemResponseDto> feedItems = List.of(createTestFeedItem());
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(feedService.getFeed(any(), any(Integer.class), any(Integer.class))).thenReturn(feedItems);

                mockMvc.perform(get("/api/feed")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data").exists())
                                .andExpect(jsonPath("$.data[0].id").exists());
        }

        @Test
        void getFeed_Unauthorized() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow())
                                .thenThrow(new SecurityException("Unauthorized"));

                mockMvc.perform(get("/api/feed")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Unauthorized"))
                                .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        void getFeedBySeason() throws Exception {
                List<FeedItemResponseDto> feedItems = List.of(createTestFeedItem());
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(feedService.getFeedBySeason(any(), any(), any(Integer.class), any(Integer.class)))
                                .thenReturn(feedItems);

                mockMvc.perform(get("/api/feed/season/SUMMER")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data").exists())
                                .andExpect(jsonPath("$.data[0].id").exists());
        }

        @Test
        void getFeedBySeason_Unauthorized() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow())
                                .thenThrow(new SecurityException("Unauthorized"));

                mockMvc.perform(get("/api/feed/season/SUMMER")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Unauthorized"))
                                .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        void getFeedByCategory() throws Exception {
                List<FeedItemResponseDto> feedItems = List.of(createTestFeedItem());
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(feedService.getFeedByCategory(any(), any(), any(Integer.class), any(Integer.class)))
                                .thenReturn(feedItems);

                mockMvc.perform(get("/api/feed/category/CASUAL")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data").exists())
                                .andExpect(jsonPath("$.data[0].id").exists());
        }

        @Test
        void getFeedByCategory_Unauthorized() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow())
                                .thenThrow(new SecurityException("Unauthorized"));

                mockMvc.perform(get("/api/feed/category/CASUAL")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Unauthorized"))
                                .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        void getUserPosts() throws Exception {
                Post testPost = createTestPost();
                Page<Post> postPage = new PageImpl<>(List.of(testPost));
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(feedService.getUserPosts(eq(1L), eq(1L), any())).thenReturn(postPage);

                mockMvc.perform(get("/api/feed/users/1/posts")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data").exists())
                                .andExpect(jsonPath("$.data.content[0].id").value(1L));
        }

        @Test
        void getUserPosts_NotFound() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(feedService.getUserPosts(eq(999L), eq(1L), any()))
                                .thenThrow(new ResourceNotFoundException("User posts not found"));

                mockMvc.perform(get("/api/feed/users/999/posts")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("User posts not found"))
                                .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        void getUserPosts_Empty() throws Exception {
                Page<Post> emptyPage = new PageImpl<>(Collections.emptyList());
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(feedService.getUserPosts(eq(1L), eq(1L), any())).thenReturn(emptyPage);

                mockMvc.perform(get("/api/feed/users/1/posts")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success").value(true))
                                .andExpect(jsonPath("$.data.content").isArray())
                                .andExpect(jsonPath("$.data.content").isEmpty());
        }

        @Test
        void getUserPosts_Unauthorized() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow())
                                .thenThrow(new SecurityException("Unauthorized"));

                mockMvc.perform(get("/api/feed/users/1/posts")
                                .param("page", "0")
                                .param("size", "20"))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.success").value(false))
                                .andExpect(jsonPath("$.message").value("Unauthorized"))
                                .andExpect(jsonPath("$.data").doesNotExist());
        }

        private FeedItemResponseDto createTestFeedItem() {
                return FeedItemResponseDto.builder()
                                .id(1L)
                                .title("Test Feed Item")
                                .content("Test content")
                                .season("SUMMER")
                                .category("CASUAL")
                                .likesCount(5)
                                .commentsCount(2)
                                .featureImage("https://example.com/feature.jpg")
                                .outfitImage("https://example.com/outfit.jpg")
                                .itemImages(Set.of("https://example.com/item1.jpg"))
                                .user(UserResponseDto.builder()
                                                .id(1L)
                                                .username("testuser")
                                                .build())
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
        }

        private Post createTestPost() {
                Post post = new Post();
                post.setId(1L);
                post.setTitle("Test Post");
                post.setContent("Test content");
                return post;
        }
}
