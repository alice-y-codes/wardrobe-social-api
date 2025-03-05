package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.enums.PostVisibility;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.FeedService;
import com.yalice.wardrobe_social_app.interfaces.FriendshipService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedServiceImpl extends BaseService implements FeedService {

    private final PostRepository postRepository;
    private final FriendshipService friendshipService;
    private final UserSearchService userSearchService;

    @Autowired
    public FeedServiceImpl(PostRepository postRepository, FriendshipService friendshipService, UserSearchService userSearchService) {
        this.postRepository = postRepository;
        this.friendshipService = friendshipService;
        this.userSearchService = userSearchService;
    }

    @Override
    public Page<Post> getUserFeed(Long userId, Pageable pageable) {
        List<User> friends = friendshipService.getFriends(userId);
        List<Long> friendIds = friends.stream()
                .map(User::getId)
                .collect(Collectors.toList());

        friendIds.add(userId);

        return postRepository.findFeedPostsForUser(friendIds, pageable);
    }

    @Override
    public Page<Post> getUserPosts(Long userId, Long viewerId, Pageable pageable) {
        User user = userSearchService.getUserEntityById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        // Determine the visibility filter based on the relationship between user and viewer
        boolean isViewerFriend = userId.equals(viewerId) || friendshipService.areFriends(userId, viewerId);

        if (isViewerFriend) {
            // Retrieve public and friends-only posts for the user
            return postRepository.findByUserIdAndVisibilityInOrderByCreatedAtDesc(
                    userId, List.of(PostVisibility.PUBLIC, PostVisibility.FRIENDS_ONLY), pageable
            );
        } else {
            // Retrieve only public posts for the user
            return postRepository.findByUserIdAndVisibilityOrderByCreatedAtDesc(
                    userId, PostVisibility.PUBLIC, pageable
            );
        }
    }
}
