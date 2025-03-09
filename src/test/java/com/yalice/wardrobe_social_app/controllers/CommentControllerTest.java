package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.dtos.comment.CommentDto;
import com.yalice.wardrobe_social_app.dtos.comment.CommentResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private AuthUtils authUtils;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(commentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        testUser = User.builder().id(1L).build();
    }

    @Test
    void createComment() throws Exception {
        CommentDto commentDto = createTestCommentDto();
        CommentResponseDto responseDto = createTestCommentResponse();
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(commentService.createComment(any(), any(), any())).thenReturn(responseDto);

        mockMvc.perform(post("/api/comments/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    void createComment_Unauthorized() throws Exception {
        CommentDto commentDto = createTestCommentDto();
        when(authUtils.getCurrentUserOrElseThrow())
                .thenThrow(new SecurityException("Unauthorized"));

        mockMvc.perform(post("/api/comments/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateComment() throws Exception {
        CommentDto commentDto = createTestCommentDto();
        CommentResponseDto responseDto = createTestCommentResponse();
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(commentService.updateComment(any(), any(), any())).thenReturn(responseDto);

        mockMvc.perform(put("/api/comments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    void updateComment_Unauthorized() throws Exception {
        CommentDto commentDto = createTestCommentDto();
        when(authUtils.getCurrentUserOrElseThrow())
                .thenThrow(new SecurityException("Unauthorized"));

        mockMvc.perform(put("/api/comments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateComment_NotFound() throws Exception {
        CommentDto commentDto = createTestCommentDto();
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        when(commentService.updateComment(any(), any(), any()))
                .thenThrow(new ResourceNotFoundException("Comment not found"));

        mockMvc.perform(put("/api/comments/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Comment not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void deleteComment() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        doNothing().when(commentService).deleteComment(any(), any());

        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteComment_Unauthorized() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow())
                .thenThrow(new SecurityException("Unauthorized"));

        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unauthorized"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void deleteComment_NotFound() throws Exception {
        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
        doThrow(new ResourceNotFoundException("Comment not found"))
                .when(commentService).deleteComment(any(), any());

        mockMvc.perform(delete("/api/comments/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Comment not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getPostComments() throws Exception {
        List<CommentResponseDto> comments = List.of(createTestCommentResponse());
        when(commentService.getPostComments(any())).thenReturn(comments);

        mockMvc.perform(get("/api/comments/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data[0].id").exists());
    }

    @Test
    void getPostComments_NotFound() throws Exception {
        when(commentService.getPostComments(any()))
                .thenThrow(new ResourceNotFoundException("Post not found"));

        mockMvc.perform(get("/api/comments/posts/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Post not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getComment() throws Exception {
        CommentResponseDto responseDto = createTestCommentResponse();
        when(commentService.getComment(any())).thenReturn(responseDto);

        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").exists());
    }

    @Test
    void getComment_NotFound() throws Exception {
        when(commentService.getComment(any()))
                .thenThrow(new ResourceNotFoundException("Comment not found"));

        mockMvc.perform(get("/api/comments/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Comment not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    private CommentDto createTestCommentDto() {
        return CommentDto.builder()
                .content("Test comment")
                .build();
    }

    private CommentResponseDto createTestCommentResponse() {
        return CommentResponseDto.builder()
                .id(1L)
                .content("Test comment")
                .userId(1L)
                .username("testuser")
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
