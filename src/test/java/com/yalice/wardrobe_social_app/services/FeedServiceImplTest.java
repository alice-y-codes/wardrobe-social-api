package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.feed.FeedItemResponseDto;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendResponseDto;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.FriendService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.mappers.FeedItemMapper;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.social.FeedServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

    @Mock
    private FeedItemMapper feedItemMapper;

    @InjectMocks
    private FeedServiceImpl feedService;

    private static final Long USER_ID = 1L;
    private static final Long VIEWER_ID = 1L;
    private static final int PAGE = 0;
    private static final int SIZE = 5;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
        return new PageImpl<>(List.of(post), PageRequest.of(PAGE, SIZE), 1);
    }

    @Test
    void shouldReturnFeedForUser() {
        when(friendService.getFriends(USER_ID)).thenReturn(mockFriendships());
        when(postRepository.findByProfileIdInOrderByCreatedAtDesc(anyList(), any())).thenReturn(mockPosts("Outfit Post"));

        FeedItemResponseDto responseDto = new FeedItemResponseDto();
        responseDto.setTitle("Outfit Post");
        when(feedItemMapper.toResponseDto(any(Post.class))).thenReturn(responseDto);

        List<FeedItemResponseDto> feed = feedService.getFeed(USER_ID, PAGE, SIZE);

        assertNotNull(feed);
        assertEquals(1, feed.size());
        assertEquals("Outfit Post", feed.getFirst().getTitle());
    }

    @Test
    void shouldReturnFeedFilteredBySeason() {
        String season = "Winter";
        when(friendService.getFriends(USER_ID)).thenReturn(mockFriendships());
        when(postRepository.findByProfileIdInAndOutfitSeasonOrderByCreatedAtDesc(anyList(), eq(season), any()))
                .thenReturn(mockPosts("Winter Outfit Post"));

        FeedItemResponseDto responseDto = new FeedItemResponseDto();
        responseDto.setTitle("Winter Outfit Post");
        when(feedItemMapper.toResponseDto(any(Post.class))).thenReturn(responseDto);

        List<FeedItemResponseDto> feed = feedService.getFeedBySeason(USER_ID, season, PAGE, SIZE);

        assertNotNull(feed);
        assertEquals(1, feed.size());
        assertEquals("Winter Outfit Post", feed.getFirst().getTitle());
    }

    @Test
    void shouldReturnFeedFilteredByCategory() {
        String category = "Casual";
        when(friendService.getFriends(USER_ID)).thenReturn(mockFriendships());
        when(postRepository.findByProfileIdInAndOutfitCategoryOrderByCreatedAtDesc(anyList(), eq(category), any()))
                .thenReturn(mockPosts("Casual Outfit Post"));

        FeedItemResponseDto responseDto = new FeedItemResponseDto();
        responseDto.setTitle("Casual Outfit Post");
        when(feedItemMapper.toResponseDto(any(Post.class))).thenReturn(responseDto);

        List<FeedItemResponseDto> feed = feedService.getFeedByCategory(USER_ID, category, PAGE, SIZE);

        assertNotNull(feed);
        assertEquals(1, feed.size());
        assertEquals("Casual Outfit Post", feed.getFirst().getTitle());
    }

    @Test
    void shouldReturnUserPosts() {
        User user = new User();
        user.setId(USER_ID);
        when(userSearchService.getUserEntityById(USER_ID)).thenReturn(user);
        when(postRepository.findByProfileIdAndVisibilityInOrderByCreatedAtDesc(eq(USER_ID), any(), any()))
                .thenReturn(mockPosts("User Post"));

        Page<Post> userPosts = feedService.getUserPosts(USER_ID, VIEWER_ID, PageRequest.of(PAGE, SIZE));

        assertNotNull(userPosts);
        assertEquals(1, userPosts.getTotalElements());
        assertEquals("User Post", userPosts.getContent().getFirst().getTitle());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        Long nonExistentUserId = 999L;
        when(userSearchService.getUserEntityById(nonExistentUserId)).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> feedService.getUserPosts(nonExistentUserId, VIEWER_ID, PageRequest.of(PAGE, SIZE)));

        assertEquals("User not found with id: 999", exception.getMessage());
    }
}
