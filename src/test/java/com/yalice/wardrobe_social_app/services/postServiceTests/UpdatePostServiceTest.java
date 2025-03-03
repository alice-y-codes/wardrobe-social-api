package com.yalice.wardrobe_social_app.services.postServiceTests;

import com.yalice.wardrobe_social_app.entities.Like;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.enums.PostVisibility;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.repositories.LikeRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpdatePostServiceTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserService userService;
    
    @InjectMocks
    private PostServiceImpl postService;

    private User user1;
    private User user2;
    private Post post;
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
    void updatePost_updatesPost() {
        // Arrange
        Long postId = 1L;

        Post updatedPost = Post.builder()
                .id(postId)
                .user(user1)
                .content("updated test post comment")
                .outfit(outfit)
                .visibility(PostVisibility.PUBLIC)
                .build();

        when(postRepository.findById(eq(postId))).thenReturn(Optional.of(post));
        when(postRepository.saveAndFlush(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Post result = postService.updatePost(postId, updatedPost);

        // Assert
        assertNotNull(result);
        assertThat(result.getContent()).isEqualTo("updated test post comment");
        assertThat(result.getId()).isEqualTo(postId);

        // Verify
        verify(postRepository).findById(postId);
        verify(postRepository).saveAndFlush(any(Post.class));
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
