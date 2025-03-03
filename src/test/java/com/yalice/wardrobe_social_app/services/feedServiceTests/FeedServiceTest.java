package com.yalice.wardrobe_social_app.services.feedServiceTests;

import com.yalice.wardrobe_social_app.entities.*;
import com.yalice.wardrobe_social_app.enums.PostVisibility;
import com.yalice.wardrobe_social_app.interfaces.FriendshipService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.repositories.CommentRepository;
import com.yalice.wardrobe_social_app.repositories.LikeRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.FeedServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FeedServiceTest {

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
    private FeedServiceImpl feedService;

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

        comment = Comment.builder()
                .id(1L)
                .post(post)
                .user(user2)
                .content("Test comment")
                .build();

        like = Like.builder()
                .id(1L)
                .post(post)
                .user(user2)
                .build();
    }

    @Test
    void getUserFeed_returnsFeedPosts() {
        // Arrange
        when(friendshipService.getFriends(anyLong())).thenReturn(Arrays.asList(user2));
        when(postRepository.findFeedPostsForUser(anyList(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(post)));

        // Act
        Page<Post> result = feedService.getUserFeed(1L, Pageable.unpaged());

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals(post, result.getContent().get(0));
    }


}