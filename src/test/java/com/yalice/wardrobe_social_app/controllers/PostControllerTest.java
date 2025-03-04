package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.dtos.post.PostDto;
import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.PostNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.PostService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.utilities.CurrentUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static com.yalice.wardrobe_social_app.utils.AuthenticationTestUtils.setupAuthentication;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Unit tests for the PostController class, covering all the CRUD operations for posts
 * and verifying various success and failure scenarios.
 */
class PostControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @Mock
    private CurrentUser currentUser;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostController postController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private User mockUser;
    private PostDto postDto;
    private PostResponseDto postResponseDto;

    /**
     * Sets up the test environment by initializing mocks and setting up the necessary objects.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();

        mockUser =User.builder()
                .id(1L)
                .username("testUser")
                .build();

        postDto = PostDto.builder()
                .title("Test Post")
                .content("This is a test post.")
                .build();

        postResponseDto = PostResponseDto.builder()
                .id(1L)
                .title("Test Post")
                .content("This is a test post")
                .build();
    }

    /**
     * Clears the security context after each test to ensure that each test is independent.
     */
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Tests the creation of a new post and checks that the response contains the created post's data.
     * Expects a successful creation response (HTTP status 201).
     *
     * @throws Exception if the request fails
     */
    @Test
    void createPost_shouldReturnCreatedPost() throws Exception {
        when(currentUser.getCurrentUserOrElseThrow()).thenReturn(mockUser);
        when(userService.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(postService.createPost(anyLong(), any(PostDto.class))).thenReturn(postResponseDto);

        setupAuthentication("testUser");

        mockMvc.perform(post("/api/feed/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Test Post"));
    }

    /**
     * Tests fetching a post by its ID and verifies the response contains the correct post data.
     * Expects a successful response (HTTP status 200).
     *
     * @throws Exception if the request fails
     */
    @Test
    void getPost_shouldReturnPost() throws Exception {
        when(currentUser.getCurrentUserOrElseThrow()).thenReturn(mockUser);
        when(userService.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(postService.getPost(anyLong(), anyLong())).thenReturn(postResponseDto);

        setupAuthentication("testUser");

        mockMvc.perform(get("/api/feed/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Test Post"));
    }

    /**
     * Tests updating an existing post and checks that the response contains the updated post's data.
     * Expects a successful response (HTTP status 200).
     *
     * @throws Exception if the request fails
     */
    @Test
    void updatePost_shouldReturnUpdatedPost() throws Exception {
        when(currentUser.getCurrentUserOrElseThrow()).thenReturn(mockUser);
        when(userService.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(postService.updatePost(anyLong(), anyLong(), any(PostDto.class))).thenReturn(postResponseDto);

        setupAuthentication("testUser");

        mockMvc.perform(put("/api/feed/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Test Post"));
    }

    /**
     * Tests the deletion of a post and verifies that the correct success message is returned.
     * Expects a successful deletion response (HTTP status 200).
     *
     * @throws Exception if the request fails
     */
    @Test
    void deletePost_shouldReturnSuccessMessage() throws Exception {
        when(currentUser.getCurrentUserOrElseThrow()).thenReturn(mockUser);
        when(userService.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
        doNothing().when(postService).deletePost(anyLong(), anyLong());

        setupAuthentication("testUser");

        mockMvc.perform(delete("/api/feed/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Post deleted successfully"));
    }

    /**
     * Tests liking a post and verifies that the correct success message is returned.
     * Expects a successful like response (HTTP status 200).
     *
     * @throws Exception if the request fails
     */
    @Test
    void likePost_shouldReturnSuccessMessage() throws Exception {
        when(currentUser.getCurrentUserOrElseThrow()).thenReturn(mockUser);
        when(userService.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(postService.likePost(anyLong(), anyLong())).thenReturn(true);

        setupAuthentication("testUser");

        mockMvc.perform(post("/api/feed/1/like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Post liked successfully"));
    }

    /**
     * Tests unliking a post and verifies that the correct success message is returned.
     * Expects a successful un-like response (HTTP status 200).
     *
     * @throws Exception if the request fails
     */
    @Test
    void unlikePost_shouldReturnSuccessMessage() throws Exception {
        when(currentUser.getCurrentUserOrElseThrow()).thenReturn(mockUser);
        when(userService.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(postService.unlikePost(anyLong(), anyLong())).thenReturn(true);

        setupAuthentication("testUser");

        mockMvc.perform(delete("/api/feed/1/like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Post unliked successfully"));
    }

    /**
     * Tests the scenario where a post is not found and verifies that the appropriate error message
     * and HTTP status 404 (Not Found) are returned.
     *
     * @throws Exception if the request fails
     */
    @Test
    void getPost_whenPostNotFound_shouldReturnNotFound() throws Exception {
        when(currentUser.getCurrentUserOrElseThrow()).thenReturn(mockUser);
        when(userService.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
        when(postService.getPost(anyLong(), anyLong())).thenThrow(new PostNotFoundException("Post not found"));

        setupAuthentication("testUser");

        mockMvc.perform(get("/api/feed/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Post not found"));
    }

//    /**
//     * Tests the scenario where a user is unauthorized to access a post and verifies that
//     * the response returns a 401 Unauthorized status and the appropriate message.
//     *
//     * @throws Exception if the request fails
//     */
//    @Test
//    void getPost_whenUserNotAuthorized_shouldReturnUnauthorized() throws Exception {
//        when(currentUser.getCurrentUserOrElseThrow()).thenReturn(mockUser);
//        when(userService.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
//        when(postService.getPost(anyLong(), anyLong())).thenReturn(postResponseDto);
//
//        setupAuthentication("anotherUser"); // Simulating a different user accessing the post
//
//        mockMvc.perform(get("/api/feed/1"))
//                .andExpect(status().isUnauthorized()) // Expecting 401 Unauthorized
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Unauthorized"));
//    }
//
//    /**
//     * Tests the scenario where a user tries to update a post that they do not own,
//     * and verifies that the response returns a 403 Forbidden status and the appropriate message.
//     *
//     * @throws Exception if the request fails
//     */
//    @Test
//    void updatePost_whenUserNotOwner_shouldReturnForbidden() throws Exception {
//        when(currentUser.getCurrentUserOrElseThrow()).thenReturn(mockUser);
//        when(userService.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
//        when(postService.updatePost(anyLong(), anyLong(), any(PostDto.class)))
//                .thenThrow(new ForbiddenActionException("You are not allowed to modify this post"));
//
//        setupAuthentication("testUser");
//
//        mockMvc.perform(put("/api/feed/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(postDto)))
//                .andExpect(status().isForbidden()) // Expecting 403 Forbidden
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("You are not allowed to modify this post"));
//    }
//
//    /**
//     * Tests the creation of a post when invalid data is provided.
//     * Expects a 400 Bad Request response and an appropriate error message.
//     *
//     * @throws Exception if the request fails
//     */
//    @Test
//    void createPost_whenBadRequest_shouldReturnBadRequest() throws Exception {
//        // Create an invalid PostDto with missing fields (or invalid data)
//        PostDto invalidPostDto = PostDto.builder()
//                .title("") // Missing required title
//                .content("") // Missing required content
//                .build();
//
//        mockMvc.perform(post("/api/feed/post")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidPostDto)))
//                .andExpect(status().isBadRequest()) // Expecting 400 Bad Request
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Invalid post data"));
//    }
//
//    /**
//     * Tests the creation of a post when an internal server error occurs.
//     * Expects a 500 Internal Server Error response and the appropriate error message.
//     *
//     * @throws Exception if the request fails
//     */
//    @Test
//    void createPost_whenInternalServerError_shouldReturnInternalServerError() throws Exception {
//        when(currentUser.getCurrentUserOrElseThrow()).thenReturn(mockUser);
//        when(userService.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
//        when(postService.createPost(anyLong(), any(PostDto.class)))
//                .thenThrow(new RuntimeException("Unexpected server error"));
//
//        setupAuthentication("testUser");
//
//        mockMvc.perform(post("/api/feed/post")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(postDto)))
//                .andExpect(status().isInternalServerError()) // Expecting 500 Internal Server Error
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
//    }
//
//    /**
//     * Tests the scenario where a client sends a POST request to a GET endpoint.
//     * Expects a 405 Method Not Allowed response and the appropriate error message.
//     *
//     * @throws Exception if the request fails
//     */
//    @Test
//    void getPost_whenUsingPostInsteadOfGet_shouldReturnMethodNotAllowed() throws Exception {
//        mockMvc.perform(post("/api/feed/1"))
//                .andExpect(status().isMethodNotAllowed()) // Expecting 405 Method Not Allowed
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Method Not Allowed"));
//    }
//
//    /**
//     * Tests the scenario where post creation validation fails due to invalid data.
//     * Expects a 400 Bad Request response and the appropriate validation error message.
//     *
//     * @throws Exception if the request fails
//     */
//    @Test
//    void createPost_whenValidationFails_shouldReturnBadRequest() throws Exception {
//        // Simulate a validation exception (e.g., title too short)
//        PostDto invalidPostDto = PostDto.builder()
//                .title("t") // Invalid title (too short)
//                .content("This is a test post.")
//                .build();
//
//        mockMvc.perform(post("/api/feed/post")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidPostDto)))
//                .andExpect(status().isBadRequest()) // Expecting 400 Bad Request
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Validation failed"));
//    }
//
//    /**
//     * Tests the scenario where a post is not found when trying to delete it.
//     * Expects a 404 Not Found response and the appropriate error message.
//     *
//     * @throws Exception if the request fails
//     */
//    @Test
//    void deletePost_whenPostNotFound_shouldReturnNotFound() throws Exception {
//        when(currentUser.getCurrentUserOrElseThrow()).thenReturn(mockUser);
//        when(userService.findUserByUsername("testUser")).thenReturn(Optional.of(mockUser));
//        doThrow(new PostNotFoundException("Post not found"))
//                .when(postService).deletePost(anyLong(), anyLong());
//
//        setupAuthentication("testUser");
//
//        mockMvc.perform(delete("/api/feed/1"))
//                .andExpect(status().isNotFound()) // Expecting 404 Not Found
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Post not found"));
//    }
//
//    /**
//     * Tests the scenario where an invalid post ID is provided, and the server responds
//     * with a 400 Bad Request status and the appropriate error message.
//     *
//     * @throws Exception if the request fails
//     */
//    @Test
//    void getPost_whenInvalidPostId_shouldReturnBadRequest() throws Exception {
//        mockMvc.perform(get("/api/feed/invalid-id"))
//                .andExpect(status().isBadRequest()) // Expecting 400 Bad Request
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Invalid post ID"));
//    }
//
//    /**
//     * Tests the scenario where an internal server error occurs while updating a post.
//     * Expects a 500 Internal Server Error response and the appropriate error message.
//     *
//     * @throws Exception if the request fails
//     */
//    @Test
//    void updatePost_whenServiceThrowsException_shouldReturnInternalServerError() throws Exception {
//        when(currentUser.getCurrentUserOrElseThrow()).thenReturn(mockUser);
//        when(postService.updatePost(anyLong(), anyLong(), any(PostDto.class)))
//                .thenThrow(new RuntimeException("Service layer error"));
//
//        setupAuthentication("testUser");
//
//        mockMvc.perform(put("/api/feed/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(postDto)))
//                .andExpect(status().isInternalServerError()) // Expecting 500 Internal Server Error
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("An error occurred while processing your request"));
//    }
}
