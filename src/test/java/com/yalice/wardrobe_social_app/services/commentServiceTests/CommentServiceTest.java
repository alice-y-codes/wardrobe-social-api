package com.yalice.wardrobe_social_app.services.commentServiceTests;

import com.yalice.wardrobe_social_app.entities.*;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.repositories.CommentRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommentServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserSearchService userSearchService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Post post;
    private Outfit outfit;
    private Comment comment;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();

        outfit = Outfit.builder()
                .id(1L)
                .name("Test Outfit")
                .user(user)
                .build();

        post = Post.builder()
                .id(1L)
                .user(user)
                .content("Test post content")
                .outfit(outfit)
                .visibility(PostVisibility.PUBLIC)
                .build();

        comment = Comment.builder()
                .id(1L)
                .post(post)
                .user(user)
                .content("Test comment")
                .build();

    }

    @Test
    void addComment_createsAndReturnsComment() {
        // Arrange
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userSearchService.findById(anyLong())).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // Act
        Comment result = commentService.addComment(1L, 2L, "Test comment");

        // Assert
        assertNotNull(result);
        assertEquals("Test comment", result.getContent());
        assertEquals(user, result.getUser());
        assertEquals(post, result.getPost());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void getPostComments_returnsComments() {
        // Arrange
        when(commentRepository.findByPostIdOrderByCreatedAtAsc(anyLong())).thenReturn(Arrays.asList(comment));

        // Act
        List<Comment> result = commentService.getPostComments(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(comment, result.get(0));
    }

    @Test
    void deleteComment_whenUserIsCommentOwner_deletesComment() {
        // Arrange
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        // Act
        commentService.deleteComment(1L, 1L);

        // Assert
        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_whenUserIsPostOwner_deletesComment() {
        // Arrange
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        // Act
        commentService.deleteComment(1L, 1L);

        // Assert
        verify(commentRepository).delete(comment);
    }
}
