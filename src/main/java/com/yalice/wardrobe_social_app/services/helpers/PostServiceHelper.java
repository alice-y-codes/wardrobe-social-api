package com.yalice.wardrobe_social_app.services.helpers;

import com.yalice.wardrobe_social_app.dtos.profile.ProfileResponseDto;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.interfaces.FriendService;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.repositories.LikeRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import org.springframework.stereotype.Component;

@Component
public class PostServiceHelper {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    private final FriendService friendService;
    private final ProfileService profileService;

    public PostServiceHelper(PostRepository postRepository, LikeRepository likeRepository, FriendService friendService, ProfileService profileService) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.friendService = friendService;
        this.profileService = profileService;
    }

    public boolean hasProfileLikedPost(Long postId, Long profileId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Profile profile = profileService.getProfileEntityById(profileId);


        return likeRepository.existsByPostAndProfile(post, profile); // Return true if the like exists
    }

    /**
     * Determines if a post is accessible to a given user based on post visibility.
     *
     * @param post the post to check
     * @param viewerId the ID of the user viewing the post
     * @return true if the user has access to the post, false otherwise
     */
    public boolean isPostAccessibleToUser(Post post, Long viewerId) {
        Long postOwnerId = post.getProfile().getId();

        // Post owner can always view their own posts
        if (postOwnerId.equals(viewerId)) {
            return true;
        }

        // Check post visibility
        Post.PostVisibility visibility = post.getVisibility();

        // Public posts are accessible to everyone
        if (visibility == Post.PostVisibility.PUBLIC) {
            return true;
        }

        // Private posts are only accessible to the owner
        if (visibility == Post.PostVisibility.PRIVATE) {
            return false;
        }

        // FRIENDS_ONLY posts are accessible to friends
        return friendService.areFriends(postOwnerId, viewerId);
    }
}
