package com.yalice.wardrobe_social_app.services.postServiceTests;

import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.PostAccessException;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.services.helpers.PostServiceHelper;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class GetPostServiceTest {

    @Mock
    private PostRepository postRepository;


    @Mock
    private PostServiceHelper postServiceHelper;

    @InjectMocks
    private PostServiceImpl postService;

    private User user1;
    private User user2;
    private Outfit outfit;
    private Post post;

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
                .visibility(PostVisibility.PUBLIC)  // Setting it to PUBLIC for testing
                .build();
    }

    @Test
    void getPost_postExistsAndAccessible_returnsPost() {
        // Arrange
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postServiceHelper.isPostAccessibleToUser(post, 1L)).thenReturn(true);

        // Act
        PostResponseDto result = postService.getPost(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(post.getId(), result.getId());
        verify(postRepository).findById(anyLong());
    }

    @Test
    void getPost_postNotFound_throwsResourceNotFoundException() {
        // Arrange
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> postService.getPost(1L, 1L));

        assertEquals("Post not found with ID: 1", exception.getMessage());
        verify(postRepository).findById(anyLong());
    }

    @Test
    void getPost_postExistsButNotAccessible_throwsPostAccessException() {
        // Arrange
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        // Simulating the post not being accessible to the viewer
        doReturn(false).when(postServiceHelper).isPostAccessibleToUser(any(Post.class), anyLong());

        // Act & Assert
        PostAccessException exception = assertThrows(PostAccessException.class, () -> {
            postService.getPost(1L, user2.getId());  // Trying to access with a different viewer ID
        });

        assertEquals("Post is not accessible to the viewer", exception.getMessage());
        verify(postRepository).findById(anyLong());
    }

    @Test
    void getPost_postExistsAndAccessibleForDifferentViewer_returnsPost() {
        // Arrange
        Post postWithFriendsOnlyVisibility = Post.builder()
                .id(1L)
                .user(user1)
                .content("Friends-only post")
                .outfit(outfit)
                .visibility(PostVisibility.FRIENDS_ONLY)
                .build();

        // Setting the post to be accessible only for friends
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(postWithFriendsOnlyVisibility));

        // Simulating that user2 is a friend of user1
        doReturn(true).when(postServiceHelper).isPostAccessibleToUser(postWithFriendsOnlyVisibility, user2.getId());

        // Act
        PostResponseDto result = postService.getPost(1L, user2.getId());

        // Assert
        assertNotNull(result);
        assertEquals(postWithFriendsOnlyVisibility.getId(), result.getId());
        verify(postRepository).findById(anyLong());
    }

    @Test
    void getPost_postWithPrivateVisibilityAndUnmatchedViewer_throwsPostAccessException() {
        // Arrange
        Post privatePost = Post.builder()
                .id(1L)
                .user(user1)
                .content("Private post")
                .visibility(PostVisibility.PRIVATE)
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(privatePost));

        // Simulating that user2 is not the post owner and cannot access the private post
        doReturn(false).when(postServiceHelper).isPostAccessibleToUser(any(Post.class), eq(user2.getId()));

        // Act & Assert
        PostAccessException exception = assertThrows(PostAccessException.class, () -> {
            postService.getPost(1L, user2.getId());  // Trying to access the private post with a different user ID
        });

        assertEquals("Post is not accessible to the viewer", exception.getMessage());
        verify(postRepository).findById(anyLong());
    }
}
