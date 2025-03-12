package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.comment.CommentDto;
import com.yalice.wardrobe_social_app.dtos.comment.CommentResponseDto;
import com.yalice.wardrobe_social_app.entities.Comment;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.mappers.CommentMapper;
import com.yalice.wardrobe_social_app.repositories.CommentRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.social.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CommentServiceImplTest {

    @Mock private CommentRepository commentRepository;
    @Mock private PostRepository postRepository;
    @Mock private ProfileService profileService;
    @Mock private CommentMapper commentMapper;
    @InjectMocks private CommentServiceImpl commentService;

    private Profile profile;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        profile = Profile.builder()
                .id(1L)
                .user(User.builder().id(1L).username("testuser").build())
                .build();
        post = Post.builder().id(1L).build();
        comment = Comment.builder().id(1L).profile(profile).post(post).content("Test comment.").build();
    }

    @Test
    void createComment_ShouldCreateComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("New comment");
        CommentResponseDto expectedResponse = new CommentResponseDto();
        expectedResponse.setContent("Test comment.");

        when(profileService.getProfileEntityById(1L)).thenReturn(profile);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponseDto(any(Comment.class))).thenReturn(expectedResponse);

        CommentResponseDto response = commentService.createComment(1L, 1L, commentDto);

        assertNotNull(response);
        assertEquals("Test comment.", response.getContent());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void createComment_ThrowsException_WhenPostNotFound() {
        when(profileService.getProfileEntityById(1L)).thenReturn(profile);
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(1L, 1L, new CommentDto()));
    }

    @Test
    void updateComment_ShouldUpdateComment() {
        Long userId = 1L, commentId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("Updated content");
        CommentResponseDto expectedResponse = new CommentResponseDto();
        expectedResponse.setContent("Updated content");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.saveAndFlush(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponseDto(any(Comment.class))).thenReturn(expectedResponse);

        CommentResponseDto response = commentService.updateComment(userId, commentId, commentDto);

        assertNotNull(response);
        assertEquals("Updated content", response.getContent());
        verify(commentRepository).saveAndFlush(any(Comment.class));
    }

    @Test
    void updateComment_ThrowsException_WhenCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(1L, 1L, new CommentDto()));
    }

    @Test
    void updateComment_ThrowsException_WhenUserDoesNotOwnComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(2L, 1L, new CommentDto()));
    }

    @Test
    void deleteComment_ShouldDeleteComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        commentService.deleteComment(1L, 1L);
        verify(commentRepository).deleteById(1L);
    }

    @Test
    void deleteComment_ThrowsException_WhenCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(1L, 1L));
    }

    @Test
    void deleteComment_ThrowsException_WhenUserDoesNotOwnComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(2L, 1L));
    }

    @Test
    void getPostComments_ShouldReturnComments() {
        CommentResponseDto expectedResponse = new CommentResponseDto();
        expectedResponse.setContent("Test comment.");

        when(commentRepository.findByPostIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(comment));
        when(commentMapper.toResponseDto(any(Comment.class))).thenReturn(expectedResponse);

        List<CommentResponseDto> response = commentService.getPostComments(1L);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Test comment.", response.getFirst().getContent());
    }

    @Test
    void getPostComments_ShouldReturnEmptyList_WhenNoCommentsFound() {
        when(commentRepository.findByPostIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());
        List<CommentResponseDto> response = commentService.getPostComments(1L);
        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void getComment_ShouldReturnComment() {
        CommentResponseDto expectedResponse = new CommentResponseDto();
        expectedResponse.setContent("Test comment.");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentMapper.toResponseDto(any(Comment.class))).thenReturn(expectedResponse);

        CommentResponseDto response = commentService.getComment(1L);
        assertNotNull(response);
        assertEquals("Test comment.", response.getContent());
    }

    @Test
    void getComment_ThrowsException_WhenCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> commentService.getComment(1L));
    }
}
