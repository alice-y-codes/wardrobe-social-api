package com.yalice.wardrobe_social_app.services.helpers;

import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.enums.PostVisibility;
import com.yalice.wardrobe_social_app.interfaces.FriendshipService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.repositories.LikeRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import org.springframework.stereotype.Component;

@Component
public class PostServiceHelper {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserSearchService userSearchService;
    private final FriendshipService friendshipService;

    public PostServiceHelper(PostRepository postRepository, LikeRepository likeRepository, UserSearchService userSearchService, FriendshipService friendshipService) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.userSearchService = userSearchService;
        this.friendshipService = friendshipService;
    }

    public boolean hasUserLikedPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        UserResponseDto user = userSearchService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return likeRepository.existsByPostAndUser(post, user); // Return true if the like exists
    }

    /**
     * Determines if a post is accessible to a given user based on post visibility.
     *
     * @param post the post to check
     * @param viewerId the ID of the user viewing the post
     * @return true if the user has access to the post, false otherwise
     */
    public boolean isPostAccessibleToUser(Post post, Long viewerId) {
        Long postOwnerId = post.getUser().getId();

        // Post owner can always view their own posts
        if (postOwnerId.equals(viewerId)) {
            return true;
        }

        // Check post visibility
        PostVisibility visibility = post.getVisibility();

        // Public posts are accessible to everyone
        if (visibility == PostVisibility.PUBLIC) {
            return true;
        }

        // Private posts are only accessible to the owner
        if (visibility == PostVisibility.PRIVATE) {
            return false;
        }

        // FRIENDS_ONLY posts are accessible to friends
        return friendshipService.areFriends(postOwnerId, viewerId);
    }
}
