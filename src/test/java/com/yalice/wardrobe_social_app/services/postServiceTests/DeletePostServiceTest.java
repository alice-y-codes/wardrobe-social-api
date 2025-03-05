package com.yalice.wardrobe_social_app.services.postServiceTests;

import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.enums.PostVisibility;
import com.yalice.wardrobe_social_app.exceptions.PostAccessException;
import com.yalice.wardrobe_social_app.exceptions.PostNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class DeletePostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserSearchService userSearchService;

    @InjectMocks
    private PostServiceImpl postService;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create mock user
        user = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();

        // Create mock outfit associated with user
        Outfit outfit = Outfit.builder()
                .id(1L)
                .name("Test Outfit")
                .user(user)
                .build();

        // Create mock post associated with user and outfit
        post = Post.builder()
                .id(1L)
                .user(user)
                .content("Test post content")
                .outfit(outfit)
                .visibility(PostVisibility.PUBLIC)
                .build();
    }

    @Test
    void deletePost_whenPostExistsAndUserIsOwner_shouldDeletePost() {
        // Arrange
        when(userSearchService.findById(anyLong())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        // Act
        postService.deletePost(post.getId(), user.getId());

        // Assert
        verify(postRepository, times(1)).deleteById(post.getId());
    }

    @Test
    void deletePost_whenPostDoesNotExist_shouldThrowResourceNotFoundException() {
        // Arrange
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () -> postService.deletePost(post.getId(), user.getId()));
        assertEquals("Post not found with ID: 1", exception.getMessage());
        verify(postRepository, never()).deleteById(anyLong());
    }

    @Test
    void deletePost_whenUserIsNotOwner_shouldThrowPostAccessException() {
        // Arrange
        User otherUser = User.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        // Act & Assert
        PostAccessException exception = assertThrows(PostAccessException.class, () -> postService.deletePost(post.getId(), otherUser.getId()));
        assertEquals("Only the post owner can delete the post", exception.getMessage());
        verify(postRepository, never()).deleteById(anyLong());
    }
}
