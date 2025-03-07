package com.yalice.wardrobe_social_app.services.commentServiceTests;

import com.yalice.wardrobe_social_app.dtos.comment.CommentDto;
import com.yalice.wardrobe_social_app.dtos.comment.CommentResponseDto;
import com.yalice.wardrobe_social_app.entities.Comment;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.repositories.CommentRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.services.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Profile profile;
    private Post post;
    private Comment comment;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        profile = Profile.builder()
                .id(1L)
                .user(User.builder()
                        .id(1L)
                        .username("testuser")
                        .build())
                .build();

        post = Post.builder()
                .id(1L)
                .build();

        comment = Comment.builder()
                .id(1L)
                .profile(profile)
                .post(post)
                .content("This is a test comment.")
                .build();

    }

    @Test
    public void createComment_ShouldCreateComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("This is a new comment.");

        when(profileService.getProfileEntityById(1L)).thenReturn(profile);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponseDto responseDto = commentService.createComment(1L, 1L, commentDto);

        assertNotNull(responseDto);
        assertEquals("This is a test comment.", responseDto.getContent());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void createComment_ThrowsException_WhenProfileNotFound() {
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("This is a new comment.");

        when(profileService.getProfileEntityById(1L)).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            commentService.createComment(1L, 1L, commentDto);
        });

        assertEquals("Profile not found with ID: 1", exception.getMessage());
    }

    @Test
    public void createComment_ThrowsException_WhenPostNotFound() {
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("This is a new comment.");

        when(profileService.getProfileEntityById(1L)).thenReturn(profile);
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            commentService.createComment(1L, 1L, commentDto);
        });

        assertEquals("Post not found with ID: 1", exception.getMessage());
    }

    @Test
    public void updateComment_ShouldUpdateComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("Updated comment content.");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponseDto responseDto = commentService.updateComment(1L, 1L, commentDto);

        assertNotNull(responseDto);
        assertEquals("Updated comment content.", responseDto.getContent());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void updateComment_ThrowsException_WhenCommentNotFound() {
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("Updated comment content.");

        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            commentService.updateComment(1L, 1L, commentDto);
        });

        assertEquals("Comment not found with ID: 1", exception.getMessage());
    }

    @Test
    public void updateComment_ThrowsException_WhenUserDoesNotOwnComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("Updated comment content.");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            commentService.updateComment(2L, 1L, commentDto);
        });

        assertEquals("User does not own this comment", exception.getMessage());
    }

    @Test
    public void deleteComment_ShouldDeleteComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L, 1L);

        verify(commentRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deleteComment_ThrowsException_WhenCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            commentService.deleteComment(1L, 1L);
        });

        assertEquals("Comment not found with ID: 1", exception.getMessage());
    }

    @Test
    public void deleteComment_ThrowsException_WhenUserDoesNotOwnComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            commentService.deleteComment(2L, 1L);
        });

        assertEquals("User does not own this comment", exception.getMessage());
    }

    @Test
    public void getPostComments_ShouldReturnComments() {
        when(commentRepository.findByPostIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(comment));

        List<CommentResponseDto> responseDtos = commentService.getPostComments(1L);

        assertNotNull(responseDtos);
        assertEquals(1, responseDtos.size());
        assertEquals("This is a test comment.", responseDtos.get(0).getContent());
    }

    @Test
    public void getPostComments_ShouldReturnEmptyList_WhenNoCommentsFound() {
        when(commentRepository.findByPostIdOrderByCreatedAtDesc(1L)).thenReturn(List.of());

        List<CommentResponseDto> responseDtos = commentService.getPostComments(1L);

        assertNotNull(responseDtos);
        assertTrue(responseDtos.isEmpty());
    }

    @Test
    public void getComment_ShouldReturnComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        CommentResponseDto responseDto = commentService.getComment(1L);

        assertNotNull(responseDto);
        assertEquals("This is a test comment.", responseDto.getContent());
    }

    @Test
    public void getComment_ThrowsException_WhenCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            commentService.getComment(1L);
        });

        assertEquals("Comment not found with ID: 1", exception.getMessage());
    }
}
