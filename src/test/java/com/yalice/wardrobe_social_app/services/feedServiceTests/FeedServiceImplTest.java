package com.yalice.wardrobe_social_app.services.feedServiceTests;

import com.yalice.wardrobe_social_app.dtos.feed.FeedItemDto;
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

    @Test
    void testGetFeed() {
        // Mock friends' data
        List<FriendResponseDto> friendships = List.of(new FriendResponseDto(2L), new FriendResponseDto(3L));
        when(friendService.getFriends(userId)).thenReturn(friendships);

        // Mock Post data
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Outfit Post");
        List<Post> posts = List.of(post);
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(page, size), posts.size());

        when(postRepository.findFeedPostsForUser(anyList(), any())).thenReturn(postPage);

        List<FeedItemDto> feed = feedService.getFeed(userId, page, size);

        assertNotNull(feed);
        assertEquals(1, feed.size());
        assertEquals("Outfit Post", feed.get(0).getTitle());
    }

    @Test
    void testGetFeedBySeason() {
        String season = "Winter";

        // Mock friends' data
        List<FriendResponseDto> friendships = List.of(new FriendResponseDto(2L), new FriendResponseDto(3L));
        when(friendService.getFriends(userId)).thenReturn(friendships);

        // Mock Post data
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Winter Outfit Post");
        List<Post> posts = List.of(post);
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(page, size), posts.size());

        when(postRepository.findFeedPostsForUserBySeason(anyList(), eq(season), any())).thenReturn(postPage);

        List<FeedItemDto> feed = feedService.getFeedBySeason(userId, season, page, size);

        assertNotNull(feed);
        assertEquals(1, feed.size());
        assertEquals("Winter Outfit Post", feed.get(0).getTitle());
    }

    @Test
    void testGetFeedByCategory() {
        String category = "Casual";

        // Mock friends' data
        List<FriendResponseDto> friendships = List.of(new FriendResponseDto(2L), new FriendResponseDto(3L));
        when(friendService.getFriends(userId)).thenReturn(friendships);

        // Mock Post data
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Casual Outfit Post");
        List<Post> posts = List.of(post);
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(page, size), posts.size());

        when(postRepository.findFeedPostsForUserByCategory(anyList(), eq(category), any())).thenReturn(postPage);

        List<FeedItemDto> feed = feedService.getFeedByCategory(userId, category, page, size);

        assertNotNull(feed);
        assertEquals(1, feed.size());
        assertEquals("Casual Outfit Post", feed.get(0).getTitle());
    }

    @Test
    void testGetUserPosts() {
        Long userId = 1L;
        Long viewerId = 1L;

        // Mock user data
        User user = new User();
        user.setId(userId);
        when(userSearchService.getUserEntityById(userId)).thenReturn(user);

        // Mock Post data
        Post post = new Post();
        post.setId(1L);
        post.setTitle("User Post");
        List<Post> posts = List.of(post);
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(page, size), posts.size());

        when(postRepository.findByUserIdAndVisibilityInOrderByCreatedAtDesc(eq(userId), any(), any()))
                .thenReturn(postPage);

        Page<Post> userPosts = feedService.getUserPosts(userId, viewerId, PageRequest.of(page, size));

        assertNotNull(userPosts);
        assertEquals(1, userPosts.getTotalElements());
        assertEquals("User Post", userPosts.getContent().get(0).getTitle());
    }

    @Test
    void testGetUserPostsNotFound() {
        Long userId = 999L;

        when(userSearchService.getUserEntityById(userId)).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> feedService.getUserPosts(userId, viewerId, PageRequest.of(page, size)));

        assertEquals("User not found with id: 999", exception.getMessage());
    }
}
