package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.enums.PostVisibility;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.FeedService;
import com.yalice.wardrobe_social_app.interfaces.FriendshipService;
import com.yalice.wardrobe_social_app.repositories.CommentRepository;
import com.yalice.wardrobe_social_app.repositories.LikeRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedServiceImpl implements FeedService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserSearchServiceImpl userService;
    private final FriendshipService friendshipService;

    @Autowired
    public FeedServiceImpl(PostRepository postRepository, CommentRepository commentRepository,
            LikeRepository likeRepository, UserSearchServiceImpl userService,
            FriendshipService friendshipService) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.userService = userService;
        this.friendshipService = friendshipService;
    }

    @Override
    public Page<Post> getUserFeed(Long userId, Pageable pageable) {
        // Get the user's friends
        List<User> friends = friendshipService.getFriends(userId);
        List<Long> friendIds = friends.stream()
                .map(User::getId)
                .collect(Collectors.toList());

        // Add the user's own ID to see their own posts
        friendIds.add(userId);

        // Get posts from friends and public posts
        return postRepository.findFeedPostsForUser(friendIds, pageable);
    }

    @Override
    public Page<Post> getUserPosts(Long userId, Long viewerId, Pageable pageable) {
        if (userService.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        // Determine the visibility filter based on the relationship between user and viewer
        boolean isViewerFriend = userId.equals(viewerId) || friendshipService.areFriends(userId, viewerId);

        // Query the database and filter posts at the query level as much as possible
        Page<Post> userPosts;
        if (isViewerFriend) {
            // Retrieve public and friends-only posts for the user
            userPosts = postRepository.findByUserIdAndVisibilityInOrderByCreatedAtDesc(
                    userId, List.of(PostVisibility.PUBLIC, PostVisibility.FRIENDS_ONLY), pageable
            );
        } else {
            // Retrieve only public posts for the user
            userPosts = postRepository.findByUserIdAndVisibilityOrderByCreatedAtDesc(
                    userId, PostVisibility.PUBLIC, pageable
            );
        }

        // Return the page of posts, which will already be filtered by visibility
        return userPosts;
    }
}