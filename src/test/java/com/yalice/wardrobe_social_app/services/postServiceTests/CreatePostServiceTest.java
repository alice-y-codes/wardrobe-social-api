package com.yalice.wardrobe_social_app.services.postServiceTests;

import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.enums.PostVisibility;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.OutfitService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreatePostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @Mock
    private OutfitService outfitService;

    @InjectMocks
    private PostServiceImpl postService;

    private User user1;
    private Outfit outfit;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user1 = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();

        outfit = Outfit.builder()
                .id(1L)
                .name("Test Outfit")
                .user(user1)
                .build();
    }

    @Test
    void createPost_userNotFound_throwsResourceNotFoundException() {
        // Arrange
        Long userId = 1L;
        String content = "Test post content";
        Long outfitId = 1L;
        PostVisibility visibility = PostVisibility.PUBLIC;

        when(userService.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(userId, content, outfitId, visibility));
    }

    @Test
    void createPost_outfitNotFound_throwsResourceNotFoundException() {
        // Arrange
        Long userId = 1L;
        String content = "Test post content";
        Long outfitId = 1L;
        PostVisibility visibility = PostVisibility.PUBLIC;

        when(userService.findById(userId)).thenReturn(Optional.of(user1));
        when(outfitService.getOutfit(outfitId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(userId, content, outfitId, visibility));
    }

    @Test
    void createPost_success_postCreated() {
        // Arrange
        Long userId = 1L;
        String content = "Test post content";
        Long outfitId = 1L;
        PostVisibility visibility = PostVisibility.PUBLIC;

        when(userService.findById(userId)).thenReturn(Optional.of(user1));
        when(outfitService.getOutfit(outfitId)).thenReturn(Optional.of(outfit));
        when(postRepository.save(any(Post.class))).thenReturn(Post.builder()
                .user(user1)
                .outfit(outfit)
                .content(content)
                .visibility(visibility)
                .build());

        // Act
        Post result = postService.createPost(userId, content, outfitId, visibility);

        // Assert
        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(visibility, result.getVisibility());
        assertEquals(user1, result.getUser());
        assertEquals(outfit, result.getOutfit());
    }

    @Test
    void createPost_userIdNull_throwsResourceNotFoundException() {
        // Arrange
        Long userId = null;
        String content = "Test post content";
        Long outfitId = 1L;
        PostVisibility visibility = PostVisibility.PUBLIC;

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(userId, content, outfitId, visibility));
    }

    @Test
    void createPost_contentNull_throwsResourceNotFoundException() {
        // Arrange
        Long userId = 1L;
        String content = null;  // null content
        Long outfitId = 1L;
        PostVisibility visibility = PostVisibility.PUBLIC;

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(userId, content, outfitId, visibility));
    }

    @Test
    void createPost_outfitIdNull_throwsResourceNotFoundException() {
        // Arrange
        Long userId = 1L;
        String content = "Test post content";
        Long outfitId = null;  // null outfitId
        PostVisibility visibility = PostVisibility.PUBLIC;

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(userId, content, outfitId, visibility));
    }

    @Test
    void createPost_validData_postCreatedWithCorrectAttributes() {
        // Arrange
        Long userId = 1L;
        String content = "Test post content";
        Long outfitId = 1L;
        PostVisibility visibility = PostVisibility.PUBLIC;

        when(userService.findById(userId)).thenReturn(Optional.of(user1));
        when(outfitService.getOutfit(outfitId)).thenReturn(Optional.of(outfit));
        when(postRepository.save(any(Post.class))).thenReturn(Post.builder()
                .user(user1)
                .outfit(outfit)
                .content(content)
                .visibility(visibility)
                .build());

        // Act
        Post result = postService.createPost(userId, content, outfitId, visibility);

        // Assert
        assertNotNull(result);
        assertEquals(user1, result.getUser());
        assertEquals(outfit, result.getOutfit());
        assertEquals(content, result.getContent());
        assertEquals(visibility, result.getVisibility());
    }
}
