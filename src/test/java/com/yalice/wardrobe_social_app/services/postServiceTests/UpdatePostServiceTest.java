package com.yalice.wardrobe_social_app.services.postServiceTests;

import com.yalice.wardrobe_social_app.entities.Like;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.enums.PostVisibility;
import com.yalice.wardrobe_social_app.helpers.PostServiceHelper;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.repositories.LikeRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.PostServiceImpl;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

public class UpdatePostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserService userService;

    @Mock
    private PostServiceHelper postServiceHelper;

    @InjectMocks
    private PostServiceImpl postService;

    private User user;
    private Post existingPost;
    private Post updatedPost;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup user and outfit
        user = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();

        Outfit outfit = Outfit.builder()
                .id(1L)
                .name("Test Outfit")
                .user(user)
                .build();

        post = Post.builder()
                .id(1L)
                .user(user)
                .content("Test post content")
                .likeCount(0)
                .build();

        // Existing post setup
        existingPost = Post.builder()
                .id(1L)
                .user(user)
                .content("Old post content")
                .outfit(outfit)
                .visibility(PostVisibility.PUBLIC)
                .build();

        // Updated post setup
        updatedPost = Post.builder()
                .id(1L)
                .user(user)
                .content("Updated post content")
                .outfit(outfit)
                .visibility(PostVisibility.PRIVATE)
                .build();
    }

    @Test
    void updatePost_whenPostIdIsNull_shouldThrowIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> postService.updatePost(null, updatedPost));
        assertEquals("Post ID cannot be null", exception.getMessage());
    }

    @Test
    void updatePost_whenPostObjectIsNull_shouldThrowIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> postService.updatePost(existingPost.getId(), null));
        assertEquals("Post object cannot be null", exception.getMessage());
    }

    @Test
    void updatePost_whenPostExistsAndHasChanges_shouldUpdatePost() {
        // Arrange
        when(postRepository.findById(existingPost.getId())).thenReturn(java.util.Optional.of(existingPost));
        when(postRepository.saveAndFlush(any(Post.class))).thenReturn(updatedPost);

        // Act
        Post result = postService.updatePost(existingPost.getId(), updatedPost);

        // Assert
        assertNotNull(result);
        assertEquals(updatedPost.getContent(), result.getContent());
        assertEquals(updatedPost.getVisibility(), result.getVisibility());
        verify(postRepository, times(1)).saveAndFlush(any(Post.class));
    }

    @Test
    void updatePost_whenPostExistsButHasNoChanges_shouldReturnExistingPost() {
        // Arrange
        Post noChangesPost = Post.builder()
                .id(existingPost.getId())
                .user(user)
                .content(existingPost.getContent()) // No content change
                .outfit(existingPost.getOutfit()) // No outfit change
                .visibility(existingPost.getVisibility()) // No visibility change
                .build();

        when(postRepository.findById(existingPost.getId())).thenReturn(java.util.Optional.of(existingPost));

        // Act
        Post result = postService.updatePost(existingPost.getId(), noChangesPost);

        // Assert
        assertNotNull(result);
        assertEquals(existingPost.getContent(), result.getContent());
        assertEquals(existingPost.getVisibility(), result.getVisibility());
        verify(postRepository, never()).saveAndFlush(any(Post.class));
    }

    @Test
    void updatePost_whenPostDoesNotExist_shouldThrowResourceNotFoundException() {
        // Arrange
        when(postRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> postService.updatePost(existingPost.getId(), updatedPost));
        assertEquals("Post not found with ID: 1", exception.getMessage());
    }

    @Test
    void likePost_whenPostOrUserDoesNotExist_shouldThrowIllegalArgumentException() {
        // Arrange
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userService.findById(anyLong())).thenReturn(Optional.of(user));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> postService.likePost(1L, 1L));
        assertEquals("Post or user not found", exception.getMessage());

        // Test for user not found
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userService.findById(anyLong())).thenReturn(Optional.empty());

        exception = assertThrows(ResourceNotFoundException.class, () -> postService.likePost(1L, 1L));
        assertEquals("Post or user not found", exception.getMessage());
    }

    @Test
    void likePost_whenUserLikesPostForTheFirstTime_shouldLikePost() {
        // Arrange
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userService.findById(user.getId())).thenReturn(Optional.of(user));
        when(postServiceHelper.hasUserLikedPost(post.getId(), user.getId())).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(new Like());
        when(postRepository.save(post)).thenReturn(post);

        // Act
        boolean result = postService.likePost(post.getId(), user.getId());

        // Assert
        assertTrue(result);
        assertEquals(1, post.getLikeCount());
        verify(likeRepository, times(1)).save(any(Like.class));
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void likePost_whenUserHasAlreadyLikedPost_shouldReturnFalse() {
        // Arrange
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userService.findById(user.getId())).thenReturn(Optional.of(user));
        when(postServiceHelper.hasUserLikedPost(post.getId(), user.getId())).thenReturn(true);

        // Act
        boolean result = postService.likePost(post.getId(), user.getId());

        // Assert
        assertFalse(result);
        verify(likeRepository, never()).save(any(Like.class));
        verify(postRepository, never()).save(post);
    }

    @Test
    void unlikePost_whenPostOrUserDoesNotExist_shouldThrowIllegalArgumentException() {
        // Arrange
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(userService.findById(anyLong())).thenReturn(Optional.of(user));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> postService.unlikePost(1L, 1L));
        assertEquals("Post or user not found", exception.getMessage());

        // Test for user not found
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userService.findById(anyLong())).thenReturn(Optional.empty());

        exception = assertThrows(ResourceNotFoundException.class, () -> postService.unlikePost(1L, 1L));
        assertEquals("Post or user not found", exception.getMessage());
    }

    @Test
    void unlikePost_whenUserUnlikesPostSuccessfully_shouldUnlikePost() {
        // Arrange
        Like like = Like.builder()
                .post(post)
                .user(user)
                .build();
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userService.findById(user.getId())).thenReturn(Optional.of(user));
        when(postServiceHelper.hasUserLikedPost(post.getId(), user.getId())).thenReturn(true);
        when(likeRepository.findByPostAndUser(post, user)).thenReturn(Optional.of(like));
//        when(likeRepository.delete(like)).thenReturn(void);
        when(postRepository.save(post)).thenReturn(post);

        // Act
        boolean result = postService.unlikePost(post.getId(), user.getId());

        // Assert
        assertTrue(result);
        assertEquals(-1, post.getLikeCount());
        verify(likeRepository, times(1)).delete(like);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void unlikePost_whenUserHasNotLikedPostBefore_shouldReturnFalse() {
        // Arrange
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(userService.findById(user.getId())).thenReturn(Optional.of(user));
        when(postServiceHelper.hasUserLikedPost(post.getId(), user.getId())).thenReturn(false);

        // Act
        boolean result = postService.unlikePost(post.getId(), user.getId());

        // Assert
        assertFalse(result);
        verify(likeRepository, never()).delete(any(Like.class));
        verify(postRepository, never()).save(post);
    }
}
