package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.dtos.PostDto;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.PostService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.utils.AuthenticationTestUtils;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostController postController;

    private User user;
    private Post post;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        post = Post.builder()
                .id(1L)
                .user(user)
                .content("Test post content")
                .build();

        postDto = new PostDto();
        postDto.setContent("Test post content");

        AuthenticationTestUtils.setupAuthentication("testuser");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createPost_createsAndReturnsPost() throws Exception {
        when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(postService.createPost(eq(1L), anyString(), any(), any())).thenReturn(post);

        // Update the URL to match the controller's path
        mockMvc.perform(post("/api/feed/post")  // Correct URL
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(postDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test post content"));
    }

    @Test
    void deletePost_deletesPost() throws Exception {
        when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(user));

        // Update the URL to match the controller's path
        mockMvc.perform(delete("/api/feed/1"))  // Correct URL
                .andExpect(status().isOk());
    }

    @Test
    void likePost_likesPost() throws Exception {
        when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(postService.likePost(1L, 1L)).thenReturn(true);

        // Update the URL to match the controller's path
        mockMvc.perform(post("/api/feed/1/like"))  // Correct URL
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Post liked"));
    }

    @Test
    void unlikePost_unlikesPost() throws Exception {
        when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(postService.unlikePost(1L, 1L)).thenReturn(true);

        // Update the URL to match the controller's path
        mockMvc.perform(delete("/api/feed/1/like"))  // Correct URL
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Post unliked"));
    }
}
