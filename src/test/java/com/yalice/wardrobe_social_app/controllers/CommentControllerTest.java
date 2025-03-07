package com.yalice.wardrobe_social_app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yalice.wardrobe_social_app.dtos.comment.CommentDto;
import com.yalice.wardrobe_social_app.dtos.comment.CommentResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.CommentNotFoundException;
import com.yalice.wardrobe_social_app.exceptions.GlobalExceptionHandler;
import com.yalice.wardrobe_social_app.interfaces.CommentService;
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

class CommentControllerTest {

        private MockMvc mockMvc;

        @Mock
        private CommentService commentService;

        @Mock
        private AuthUtils authUtils;

        @InjectMocks
        private CommentController commentController;

        private final ObjectMapper objectMapper = new ObjectMapper();
        private User testUser;
        private CommentDto testCommentDto;
        private CommentResponseDto testCommentResponseDto;
        private List<CommentResponseDto> testCommentList;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                        .setControllerAdvice(new GlobalExceptionHandler())
                        .build();

                initializeTestData();
        }

        private void initializeTestData() {
                testUser = User.builder().username("testuser").email("test@example.com").build();
                testCommentDto = new CommentDto("Test comment");

                testCommentResponseDto = new CommentResponseDto(1L, "Test comment", LocalDateTime.now(), LocalDateTime.now(), 1L, "testuser", 1L);

                testCommentList = Arrays.asList(
                        testCommentResponseDto,
                        new CommentResponseDto(2L, "Another comment", LocalDateTime.now(), LocalDateTime.now(), 2L, "otheruser", 1L)
                );
        }

        @Test
        void createComment_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(commentService.createComment(anyLong(), anyLong(), any(CommentDto.class)))
                        .thenReturn(testCommentResponseDto);

                mockMvc.perform(post("/api/comments/posts/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testCommentDto)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.message", is("Comment created successfully")))
                        .andExpect(jsonPath("$.data.id", is(1)))
                        .andExpect(jsonPath("$.data.content", is("Test comment")))
                        .andExpect(jsonPath("$.data.username", is("testuser")))
                        .andExpect(jsonPath("$.data.postId", is(1)));

                verify(commentService).createComment(anyLong(), anyLong(), any(CommentDto.class));
        }

        @Test
        void createComment_Error() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(commentService.createComment(anyLong(), anyLong(), any(CommentDto.class)))
                        .thenThrow(new RuntimeException("Failed to create comment"));

                mockMvc.perform(post("/api/comments/posts/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testCommentDto)))
                        .andExpect(status().isInternalServerError())
                        .andExpect(jsonPath("$.success", is(false)))
                        .andExpect(jsonPath("$.message", is("Failed to create comment")));

                verify(commentService).createComment(anyLong(), anyLong(), any(CommentDto.class));
        }

        @Test
        void updateComment_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(commentService.updateComment(anyLong(), anyLong(), any(CommentDto.class)))
                        .thenReturn(testCommentResponseDto);

                mockMvc.perform(put("/api/comments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testCommentDto)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.message", is("Comment updated successfully")))
                        .andExpect(jsonPath("$.data.id", is(1)))
                        .andExpect(jsonPath("$.data.content", is("Test comment")))
                        .andExpect(jsonPath("$.data.username", is("testuser")));

                verify(commentService).updateComment(anyLong(), anyLong(), any(CommentDto.class));
        }

        @Test
        void updateComment_NotFound() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                when(commentService.updateComment(anyLong(), anyLong(), any(CommentDto.class)))
                        .thenThrow(new CommentNotFoundException("Comment not found with ID: 999"));

                mockMvc.perform(put("/api/comments/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testCommentDto)))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.success", is(false)))
                        .andExpect(jsonPath("$.message", is("Comment not found with ID: 999")));

                verify(commentService).updateComment(anyLong(), anyLong(), any(CommentDto.class));
        }

        @Test
        void deleteComment_Success() throws Exception {
                when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
                doNothing().when(commentService).deleteComment(anyLong(), anyLong());

                mockMvc.perform(delete("/api/comments/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.message", is("Comment deleted successfully")))
                        .andExpect(jsonPath("$.data", nullValue()));

                verify(commentService).deleteComment(anyLong(), anyLong());
        }

        @Test
        void getPostComments_Success() throws Exception {
                when(commentService.getPostComments(anyLong())).thenReturn(testCommentList);

                mockMvc.perform(get("/api/comments/posts/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.message", is("Comments retrieved successfully")))
                        .andExpect(jsonPath("$.data", hasSize(2)))
                        .andExpect(jsonPath("$.data[0].content", is("Test comment")))
                        .andExpect(jsonPath("$.data[1].content", is("Another comment")));

                verify(commentService).getPostComments(anyLong());
        }

        @Test
        void getComment_Success() throws Exception {
                when(commentService.getComment(anyLong())).thenReturn(testCommentResponseDto);

                mockMvc.perform(get("/api/comments/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.message", is("Comment retrieved successfully")))
                        .andExpect(jsonPath("$.data.id", is(1)))
                        .andExpect(jsonPath("$.data.content", is("Test comment")))
                        .andExpect(jsonPath("$.data.username", is("testuser")));

                verify(commentService).getComment(anyLong());
        }

        @Test
        void getComment_NotFound() throws Exception {
                when(commentService.getComment(anyLong()))
                        .thenThrow(new CommentNotFoundException("Comment not found with ID: 999"));

                mockMvc.perform(get("/api/comments/999"))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.success", is(false)))
                        .andExpect(jsonPath("$.message", is("Comment not found with ID: 999")));

                verify(commentService).getComment(anyLong());
        }
}
