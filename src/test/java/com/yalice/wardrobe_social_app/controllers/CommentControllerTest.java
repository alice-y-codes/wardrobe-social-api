package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.dtos.comment.CommentDto;
import com.yalice.wardrobe_social_app.entities.Comment;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.CommentService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
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

class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @Mock
    private UserSearchService userSearchService;

    @InjectMocks
    private CommentController commentController;

    private User user;
    private Post post;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();

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

        comment = Comment.builder()
                .id(1L)
                .post(post)
                .user(user)
                .content("Test comment")
                .build();

        commentDto = new CommentDto();
        commentDto.setContent("Test comment");

        AuthenticationTestUtils.setupAuthentication("testuser");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext(); // Clears the security context after each test
    }

    @Test
    void addComment_addsAndReturnsComment() throws Exception {
        when(userSearchService.findUserByUsername("testuser")).thenReturn(Optional.of(user));
        when(commentService.addComment(eq(1L), eq(1L), anyString())).thenReturn(comment);

        mockMvc.perform(post("/api/feed/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test comment"));
    }

    @Test
    void deleteComment_deletesComment() throws Exception {
        when(userSearchService.findUserByUsername("testuser")).thenReturn(Optional.of(user));

        mockMvc.perform(delete("/api/feed/1/comments/1"))
                .andExpect(status().isOk());
    }
}
