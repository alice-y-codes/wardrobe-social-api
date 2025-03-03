package com.yalice.wardrobe_social_app.services.postServiceTests;

import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.enums.PostVisibility;
import com.yalice.wardrobe_social_app.interfaces.FriendshipService;
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
import static org.mockito.Mockito.when;

public class GetPostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private FriendshipService friendshipService;

    @InjectMocks
    private PostServiceImpl postService;

    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        User user1 = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();


        Outfit outfit = Outfit.builder()
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
}
