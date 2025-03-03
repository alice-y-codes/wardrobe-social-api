package com.yalice.wardrobe_social_app.services.postServiceTests;

import com.yalice.wardrobe_social_app.entities.*;
import com.yalice.wardrobe_social_app.enums.PostVisibility;
import com.yalice.wardrobe_social_app.interfaces.FriendshipService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.repositories.CommentRepository;
import com.yalice.wardrobe_social_app.repositories.LikeRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserService userService;

    @Mock
    private FriendshipService friendshipService;

    @InjectMocks
    private PostServiceImpl postService;

    private User user1;
    private User user2;
    private Post post;
    private Comment comment;
    private Like like;
    private Outfit outfit;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user1 = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();

        user2 = User.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .build();

        outfit = Outfit.builder()
                .id(1L)
                .name("Test Outfit")
                .user(user1)
                .build();

        post = Post.builder()
                .id(1L)
                .user(user1)
                .content("Test post content")
                .outfit(outfit)
                .visibility(PostVisibility.PUBLIC)
                .build();

        like = Like.builder()
                .id(1L)
                .post(post)
                .user(user2)
                .build();
    }

    @Test
    void createPost_createsAndReturnsPost() {
        // Arrange
        when(userService.findById(anyLong())).thenReturn(Optional.of(user1));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        // Act
        Post result = postService.createPost(1L, "Test post content", 1L, PostVisibility.PUBLIC);

        // Assert
        assertNotNull(result);
        assertEquals("Test post content", result.getContent());
        assertEquals(PostVisibility.PUBLIC, result.getVisibility());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void getPost_whenPostExistsAndIsAccessible_returnsPost() {
        // Arrange
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(friendshipService.areFriends(anyLong(), anyLong())).thenReturn(true);

        // Act
        Optional<Post> result = postService.getPost(1L, 2L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(post, result.get());
    }

    @Test
    void getPost_whenPostIsPrivateAndNotOwner_returnsEmpty() {
        // Arrange
        post.setVisibility(PostVisibility.PRIVATE);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        // Act
        Optional<Post> result = postService.getPost(1L, 2L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void likePost_whenNotAlreadyLiked_createsLikeAndReturnsTrue() {
        // Arrange
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userService.findById(anyLong())).thenReturn(Optional.of(user2));
        when(likeRepository.existsByPostAndUser(any(Post.class), any(User.class))).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        // Act
        boolean result = postService.likePost(1L, 2L);

        // Assert
        assertTrue(result);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void unlikePost_whenLikeExists_deletesLikeAndReturnsTrue() {
        // Arrange
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userService.findById(anyLong())).thenReturn(Optional.of(user2));
        when(likeRepository.findByPostAndUser(any(Post.class), any(User.class))).thenReturn(Optional.of(like));

        // Act
        boolean result = postService.unlikePost(1L, 2L);

        // Assert
        assertTrue(result);
        verify(likeRepository).deleteByPostAndUser(post, user2);
    }
}
