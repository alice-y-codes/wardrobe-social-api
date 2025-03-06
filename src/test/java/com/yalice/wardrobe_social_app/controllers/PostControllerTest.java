package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.dtos.post.PostDto;
import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.exceptions.PostNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.PostService;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the PostController class, covering all the CRUD operations for
 * posts
 * and verifying various success and failure scenarios.
 */
class PostControllerTest {

        private MockMvc mockMvc;

        @Mock
        private PostService postService;

        @Mock
        private AuthUtils authUtils;

        @InjectMocks
        private PostController postController;

        private final ObjectMapper objectMapper = new ObjectMapper();
        private User testUser;
        private PostDto testPostDto;
        private PostResponseDto testPostResponseDto;
        private Outfit testOutfit;
        private List<PostResponseDto> testPostList;

        /**
         * Sets up the test environment by initializing mocks and setting up the
         * necessary objects.
         */
        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders.standaloneSetup(postController)
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();

                initializeTestData();
        }

        private void initializeTestData() {
                testUser = User.builder()
                                .username("testuser")
                                .email("test@example.com")
                                .build();
                testUser.setId(1L);

                testOutfit = Outfit.builder()
                                .name("Test Outfit")
                                .description("A test outfit")
                                .season("SUMMER")
                                .favorite(false)
                                .isPublic(true)
                                .build();
                testOutfit.setId(1L);
                testOutfit.setCreatedAt(LocalDateTime.now());
                testOutfit.setUpdatedAt(LocalDateTime.now());

                testPostDto = PostDto.builder()
                                .title("Test Post")
                                .content("Test post content")
                                .featureImage("https://example.com/post.jpg")
                                .outfitId(1L)
                                .visibility("PUBLIC")
                                .build();

                testPostResponseDto = PostResponseDto.builder()
                                .id(1L)
                                .title("Test Post")
                                .content("Test post content")
                                .featureImage("https://example.com/post.jpg")
                                .outfit(testOutfit)
                                .visibility("PUBLIC")
                                .username("testuser")
                                .build();

                testPostList = Arrays.asList(
                                testPostResponseDto,
                                PostResponseDto.builder()
                                                .id(2L)
                                                .title("Another Post")
                                                .content("Another post content")
                                                .featureImage("https://example.com/another-post.jpg")
                                                .outfit(testOutfit)
                                                .visibility("PUBLIC")
                                                .username("otheruser")
                                                .build());
        }

        /**
         * Tests the creation of a new post and checks that the response contains the
         * created post's data.
         * Expects a successful creation response (HTTP status 201).
         *
         * @throws Exception if the request fails
         */
        @Test
        void createPost_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(postService.createPost(anyLong(), any(PostDto.class))).thenReturn(testPostResponseDto);

                mockMvc.perform(post("/api/feed/post")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testPostDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Post created successfully")))
                                .andExpect(jsonPath("$.data.id", is(1)))
                                .andExpect(jsonPath("$.data.title", is("Test Post")))
                                .andExpect(jsonPath("$.data.content", is("Test post content")))
                                .andExpect(jsonPath("$.data.visibility", is("PUBLIC")))
                                .andExpect(jsonPath("$.data.username", is("testuser")));

                verify(postService).createPost(anyLong(), any(PostDto.class));
        }

        /**
         * Tests the scenario where a post is not found and verifies that the
         * appropriate error message
         * and HTTP status 404 (Not Found) are returned.
         *
         * @throws Exception if the request fails
         */
        @Test
        void getPost_NotFound() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(postService.getPost(anyLong(), anyLong()))
                                .thenThrow(new PostNotFoundException("Post not found with ID: 999"));

                mockMvc.perform(get("/api/feed/999"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.success", is(false)))
                                .andExpect(jsonPath("$.message", is("Post not found with ID: 999")));

                verify(postService).getPost(anyLong(), anyLong());
        }

        /**
         * Tests updating an existing post and checks that the response contains the
         * updated post's data.
         * Expects a successful response (HTTP status 200).
         *
         * @throws Exception if the request fails
         */
        @Test
        void updatePost_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(postService.updatePost(anyLong(), anyLong(), any(PostDto.class))).thenReturn(testPostResponseDto);

                mockMvc.perform(put("/api/feed/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testPostDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Post updated successfully")))
                                .andExpect(jsonPath("$.data.id", is(1)))
                                .andExpect(jsonPath("$.data.title", is("Test Post")))
                                .andExpect(jsonPath("$.data.content", is("Test post content")))
                                .andExpect(jsonPath("$.data.visibility", is("PUBLIC")));

                verify(postService).updatePost(anyLong(), anyLong(), any(PostDto.class));
        }

        /**
         * Tests the deletion of a post and verifies that the correct success message is
         * returned.
         * Expects a successful deletion response (HTTP status 200).
         *
         * @throws Exception if the request fails
         */
        @Test
        void deletePost_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                doNothing().when(postService).deletePost(anyLong(), anyLong());

                mockMvc.perform(delete("/api/feed/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Post deleted successfully")))
                                .andExpect(jsonPath("$.data", is("Post deleted")));

                verify(postService).deletePost(anyLong(), anyLong());
        }

        /**
         * Tests liking a post and verifies that the correct success message is
         * returned.
         * Expects a successful like response (HTTP status 200).
         *
         * @throws Exception if the request fails
         */
        @Test
        void likePost_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(postService.toggleLikePost(anyLong(), anyLong())).thenReturn(true);

                mockMvc.perform(post("/api/feed/1/like"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Post liked successfully")))
                                .andExpect(jsonPath("$.data", is("Post liked")));

                verify(postService).toggleLikePost(anyLong(), anyLong());
        }

        /**
         * Tests liking a post and verifies that the correct success message is
         * returned.
         * Expects a successful like response (HTTP status 200).
         *
         * @throws Exception if the request fails
         */
        @Test
        void likePost_AlreadyLiked() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(postService.toggleLikePost(anyLong(), anyLong())).thenReturn(false);

                mockMvc.perform(post("/api/feed/1/like"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Post already liked")))
                                .andExpect(jsonPath("$.data", is("Post already liked")));

                verify(postService).toggleLikePost(anyLong(), anyLong());
        }

        /**
         * Tests unliking a post and verifies that the correct success message is
         * returned.
         * Expects a successful un-like response (HTTP status 200).
         *
         * @throws Exception if the request fails
         */
        @Test
        void unlikePost_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(postService.toggleLikePost(anyLong(), anyLong())).thenReturn(true);

                mockMvc.perform(delete("/api/feed/1/like"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Post unliked successfully")))
                                .andExpect(jsonPath("$.data", is("Post unliked")));

                verify(postService).toggleLikePost(anyLong(), anyLong());
        }

        /**
         * Tests unliking a post and verifies that the correct success message is
         * returned.
         * Expects a successful un-like response (HTTP status 200).
         *
         * @throws Exception if the request fails
         */
        @Test
        void unlikePost_NotLiked() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(postService.toggleLikePost(anyLong(), anyLong())).thenReturn(false);

                mockMvc.perform(delete("/api/feed/1/like"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.success", is(true)))
                                .andExpect(jsonPath("$.message", is("Post was not liked")))
                                .andExpect(jsonPath("$.data", is("Post was not liked")));

                verify(postService).toggleLikePost(anyLong(), anyLong());
        }
}
