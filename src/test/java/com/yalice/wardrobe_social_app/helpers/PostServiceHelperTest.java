package com.yalice.wardrobe_social_app.helpers;

import static org.junit.jupiter.api.Assertions.*;

import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.Post.PostVisibility;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.interfaces.FriendService;
import com.yalice.wardrobe_social_app.services.helpers.PostServiceHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

class PostServiceHelperTest {

    @InjectMocks
    private PostServiceHelper postServiceHelper;

    @Mock
    private FriendService friendService;

    private Post post;
    private Profile postOwner;
    private Long viewerId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        postOwner = Profile.builder()
                .id(1L)
                .build();

        post = Post.builder()
                .id(1L)
                .content("Post Content")
                .profile(postOwner)
                .build();
    }

    @Test
    void testIsPostAccessibleToUser_PublicVisibility() {
        // Arrange
        post.setVisibility(Post.PostVisibility.PUBLIC);
        viewerId = 2L; // Viewer is not the owner

        // Act
        boolean result = postServiceHelper.isPostAccessibleToUser(post, viewerId);

        // Assert
        assertTrue(result, "Post with PUBLIC visibility should be accessible to everyone.");
    }

    @Test
    void testIsPostAccessibleToUser_PrivateVisibility_NotOwner() {
        // Arrange
        post.setVisibility(Post.PostVisibility.PRIVATE); // Private post
        viewerId = 2L; // Viewer is not the owner

        // Act
        boolean result = postServiceHelper.isPostAccessibleToUser(post, viewerId);

        // Assert
        assertFalse(result, "Private post should not be accessible to others.");
    }

    @Test
    void testIsPostAccessibleToUser_PrivateVisibility_Owner() {
        // Arrange
        post.setVisibility(Post.PostVisibility.PRIVATE); // Private post
        viewerId = postOwner.getId(); // Viewer is the owner

        // Act
        boolean result = postServiceHelper.isPostAccessibleToUser(post, viewerId);

        // Assert
        assertTrue(result, "Owner should always be able to access their private post.");
    }

    @Test
    void testIsPostAccessibleToUser_PublicVisibility_Owner() {
        // Arrange
        post.setVisibility(Post.PostVisibility.PUBLIC); // Public post
        viewerId = postOwner.getId(); // Viewer is the owner

        // Act
        boolean result = postServiceHelper.isPostAccessibleToUser(post, viewerId);

        // Assert
        assertTrue(result, "Owner should always be able to access their public post.");
    }

    @Test
    void testIsPostAccessibleToUser_FriendsOnly_ViewerIsFriend() {
        // Arrange
        post.setVisibility(Post.PostVisibility.FRIENDS_ONLY); // Friends-only post
        viewerId = 2L; // Viewer is not the owner
        when(friendService.areFriends(postOwner.getId(), viewerId)).thenReturn(true); // Mock friendship

        // Act
        boolean result = postServiceHelper.isPostAccessibleToUser(post, viewerId);

        // Assert
        assertTrue(result, "Friends-only post should be accessible to friends.");
    }

    @Test
    void testIsPostAccessibleToUser_FriendsOnly_ViewerIsNotFriend() {
        // Arrange
        post.setVisibility(PostVisibility.FRIENDS_ONLY); // Friends-only post
        viewerId = 2L; // Viewer is not the owner
        when(friendService.areFriends(postOwner.getId(), viewerId)).thenReturn(false); // Mock no friendship

        // Act
        boolean result = postServiceHelper.isPostAccessibleToUser(post, viewerId);

        // Assert
        assertFalse(result, "Friends-only post should not be accessible to non-friends.");
    }

    @Test
    void testIsPostAccessibleToUser_PostOwnerCanViewOwnPost() {
        // Arrange
        post.setVisibility(Post.PostVisibility.FRIENDS_ONLY); // Friends-only post
        viewerId = postOwner.getId(); // Viewer is the post owner

        // Act
        boolean result = postServiceHelper.isPostAccessibleToUser(post, viewerId);

        // Assert
        assertTrue(result, "Post owner should always be able to view their own post.");
    }
}
