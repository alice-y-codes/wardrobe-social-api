package com.yalice.wardrobe_social_app.services.postServiceTests;

import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.enums.PostVisibility;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class DeletePostServiceTest {
    @Mock
    private PostRepository postRepository;



    @Mock
    private UserService userService;


    @InjectMocks
    private PostServiceImpl postService;

    private User user1;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user1 = User.builder()
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
    void deletePost_whenPostExistsAndUserIsOwner_shouldDeletePost() {
        // Arrange
        when(userService.findById(anyLong())).thenReturn(Optional.of(user1));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        // Act
        postService.deletePost(post.getId(), user1.getId());

        // Assert
        verify(postRepository, times(1)).deleteById(post.getId());
    }
}
