package com.yalice.wardrobe_social_app.services.postServiceTests;

import com.yalice.wardrobe_social_app.dtos.PostDto;
import com.yalice.wardrobe_social_app.dtos.PostResponseDto;
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
    private PostDto postDto;

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

        // Create a PostDto with mock values
        postDto = PostDto.builder()
                .title("Test Post")
                .content("Test post content")
                .outfitId(1L)
                .visibility(PostVisibility.PUBLIC.name())
                .build();
    }

    @Test
    void createPost_userNotFound_throwsResourceNotFoundException() {
        // Arrange
        Long userId = 1L;

        when(userService.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(userId, postDto));
    }

    @Test
    void createPost_outfitNotFound_throwsResourceNotFoundException() {
        // Arrange
        Long userId = 1L;

        when(userService.findById(userId)).thenReturn(Optional.of(user1));
        when(outfitService.getOutfit(postDto.getOutfitId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(userId, postDto));
    }

    @Test
    void createPost_success_postCreated() {
        // Arrange
        Long userId = 1L;

        when(userService.findById(userId)).thenReturn(Optional.of(user1));
        when(outfitService.getOutfit(postDto.getOutfitId())).thenReturn(Optional.of(outfit));
        when(postRepository.save(any())).thenReturn(Post.builder()
                .user(user1)
                .outfit(outfit)
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .visibility(PostVisibility.valueOf(postDto.getVisibility()))
                .build());

        // Act
        PostResponseDto result = postService.createPost(userId, postDto);

        // Assert
        assertNotNull(result);
        assertEquals(postDto.getTitle(), result.getTitle());
        assertEquals(postDto.getContent(), result.getContent());
        assertEquals(postDto.getVisibility(), result.getVisibility());
        assertEquals(user1.getUsername(), result.getUsername());
        assertEquals(outfit.getId(), result.getOutfitId());
    }

    @Test
    void createPost_userIdNull_throwsResourceNotFoundException() {
        // Arrange
        Long userId = null;

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(userId, postDto));
    }

    @Test
    void createPost_contentNull_throwsResourceNotFoundException() {
        // Arrange
        Long userId = 1L;
        PostDto postDtoWithNullContent = PostDto.builder()
                .title("Test Post")
                .content(null)  // null content
                .outfitId(1L)
                .visibility(PostVisibility.PUBLIC.name())
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(userId, postDtoWithNullContent));
    }

    @Test
    void createPost_outfitIdNull_throwsResourceNotFoundException() {
        // Arrange
        Long userId = 1L;
        PostDto postDtoWithNullOutfitId = PostDto.builder()
                .title("Test Post")
                .content("Test post content")
                .outfitId(null)  // null outfitId
                .visibility(PostVisibility.PUBLIC.name())
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> postService.createPost(userId, postDtoWithNullOutfitId));
    }

    @Test
    void createPost_validData_postCreatedWithCorrectAttributes() {
        // Arrange
        Long userId = 1L;

        when(userService.findById(userId)).thenReturn(Optional.of(user1));
        when(outfitService.getOutfit(postDto.getOutfitId())).thenReturn(Optional.of(outfit));
        when(postRepository.save(any())).thenReturn(Post.builder()
                .user(user1)
                .outfit(outfit)
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .visibility(PostVisibility.valueOf(postDto.getVisibility()))
                .build());

        // Act
        PostResponseDto result = postService.createPost(userId, postDto);

        // Assert
        assertNotNull(result);
        assertEquals(postDto.getTitle(), result.getTitle());
        assertEquals(postDto.getContent(), result.getContent());
        assertEquals(postDto.getVisibility(), result.getVisibility());
        assertEquals(user1.getUsername(), result.getUsername());
        assertEquals(outfit.getId(), result.getOutfitId());
    }
}
