package com.yalice.wardrobe_social_app.services.feedServiceTests;

import com.yalice.wardrobe_social_app.dtos.feed.FeedItemResponseDto;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendResponseDto;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.FriendService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.FeedServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeedServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private FriendService friendService;

    @Mock
    private UserSearchService userSearchService;

    @InjectMocks
    private FeedServiceImpl feedService;

    private Long userId;
    private Long viewerId;
    private int page;
    private int size;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 1L;
        viewerId = 1L;
        page = 0;
        size = 5;
    }

    private List<FriendResponseDto> mockFriendships() {
        return List.of(
                FriendResponseDto.builder().id(2L).build(),
                FriendResponseDto.builder().id(3L).build()
        );
    }

    private Page<Post> mockPosts(String title) {
        Post post = new Post();
        post.setId(1L);
        post.setTitle(title);
        List<Post> posts = List.of(post);
        return new PageImpl<>(posts, PageRequest.of(page, size), posts.size());
    }

    @Test
    void testGetFeed() {
        // Arrange
        when(friendService.getFriends(userId)).thenReturn(mockFriendships());
        when(postRepository.findFeedPostsForUser(anyList(), any())).thenReturn(mockPosts("Outfit Post"));

        // Act
        List<FeedItemResponseDto> feed = feedService.getFeed(userId, page, size);

        // Assert
        assertNotNull(feed);
        assertEquals(1, feed.size());
        assertEquals("Outfit Post", feed.getFirst().getTitle());
    }

    @Test
    void testGetFeedBySeason() {
        // Arrange
        String season = "Winter";
        when(friendService.getFriends(userId)).thenReturn(mockFriendships());
        when(postRepository.findFeedPostsForUserBySeason(anyList(), eq(season), any())).thenReturn(mockPosts("Winter Outfit Post"));

        // Act
        List<FeedItemResponseDto> feed = feedService.getFeedBySeason(userId, season, page, size);

        // Assert
        assertNotNull(feed);
        assertEquals(1, feed.size());
        assertEquals("Winter", feed.getFirst().getSeason());
    }

    @Test
    void testGetFeedByCategory() {
        // Arrange
        String category = "Casual";
        when(friendService.getFriends(userId)).thenReturn(mockFriendships());
        when(postRepository.findFeedPostsForUserByCategory(anyList(), eq(category), any())).thenReturn(mockPosts("Casual Outfit Post"));

        // Act
        List<FeedItemResponseDto> feed = feedService.getFeedByCategory(userId, category, page, size);

        // Assert
        assertNotNull(feed);
        assertEquals(1, feed.size());
        assertEquals("Casual Outfit Post", feed.getFirst().getTitle());
    }

    @Test
    void testGetUserPosts() {
        // Arrange
        User user = new User();
        user.setId(userId);
        when(userSearchService.getUserEntityById(userId)).thenReturn(user);
        when(postRepository.findByUserIdAndVisibilityInOrderByCreatedAtDesc(eq(userId), any(), any()))
                .thenReturn(mockPosts("User Post"));

        // Act
        Page<Post> userPosts = feedService.getUserPosts(userId, viewerId, PageRequest.of(page, size));

        // Assert
        assertNotNull(userPosts);
        assertEquals(1, userPosts.getTotalElements());
        assertEquals("User Post", userPosts.getContent().getFirst().getTitle());
    }

    @Test
    void testGetUserPostsNotFound() {
        // Arrange
        Long userId = 999L;
        when(userSearchService.getUserEntityById(userId)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> feedService.getUserPosts(userId, viewerId, PageRequest.of(page, size)));
        assertEquals("User not found with id: 999", exception.getMessage());
    }
}
