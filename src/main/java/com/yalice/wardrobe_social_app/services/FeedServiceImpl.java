package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.feed.FeedItemResponseDto;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendResponseDto;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.FeedService;
import com.yalice.wardrobe_social_app.interfaces.FriendService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import com.yalice.wardrobe_social_app.services.helpers.DtoConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedServiceImpl extends BaseService implements FeedService {

        private final PostRepository postRepository;
        private final FriendService friendService;
        private final UserSearchService userSearchService;
        private final DtoConversionService dtoConversionService;

        @Autowired
        public FeedServiceImpl(
                PostRepository postRepository,
                FriendService friendService,
                UserSearchService userSearchService,
                DtoConversionService dtoConversionService)
        {
                this.postRepository = postRepository;
                this.friendService = friendService;
                this.userSearchService = userSearchService;
                this.dtoConversionService = dtoConversionService;
        }

        // Helper method to get friendIds for a user
        private List<Long> getFriendIds(Long userId) {
                List<FriendResponseDto> friendships = friendService.getFriends(userId);
                List<Long> friendIds = friendships.stream()
                        .map(FriendResponseDto::getUserId)
                        .collect(Collectors.toList());
                friendIds.add(userId);  // Include the user itself in the friend list
                return friendIds;
        }

        @Override
        public List<FeedItemResponseDto> getFeed(Long userId, int page, int size) {
                logger.info("Retrieving feed for user ID: {} (page: {}, size: {})", userId, page, size);

                List<Long> friendIds = getFriendIds(userId);
                Page<Post> posts = postRepository.findByProfileIdInOrderByCreatedAtDesc(friendIds, Pageable.ofSize(size).withPage(page));

                return convertPostsToFeedItems(posts);
        }

        @Override
        public List<FeedItemResponseDto> getFeedBySeason(Long userId, String season, int page, int size) {
                logger.info("Retrieving feed filtered by season '{}' for user ID: {} (page: {}, size: {})", season, userId, page, size);

                List<Long> friendIds = getFriendIds(userId);
                Page<Post> posts = postRepository.findByProfileIdInAndOutfitSeasonOrderByCreatedAtDesc(friendIds, season, Pageable.ofSize(size).withPage(page));

                return convertPostsToFeedItems(posts);
        }

        @Override
        public List<FeedItemResponseDto> getFeedByCategory(Long userId, String category, int page, int size) {
                logger.info("Retrieving feed filtered by category '{}' for user ID: {} (page: {}, size: {})", category, userId, page, size);

                List<Long> friendIds = getFriendIds(userId);
                Page<Post> posts = postRepository.findByProfileIdInAndOutfitCategoryOrderByCreatedAtDesc(friendIds, category, Pageable.ofSize(size).withPage(page));

                return convertPostsToFeedItems(posts);
        }

        @Override
        public Page<Post> getUserPosts(Long userId, Long viewerId, Pageable pageable) {
                logger.info("Retrieving posts for user ID: {} viewed by user ID: {}", userId, viewerId);

                User user = userSearchService.getUserEntityById(userId);
                if (user == null) {
                        throw new ResourceNotFoundException("User not found with id: " + userId);
                }

                boolean isViewerFriend = userId.equals(viewerId) || friendService.areFriends(userId, viewerId);

                if (isViewerFriend) {
                        return postRepository.findByProfileIdAndVisibilityInOrderByCreatedAtDesc(
                                userId, List.of(Post.PostVisibility.PUBLIC, Post.PostVisibility.FRIENDS_ONLY), pageable);
                } else {
                        return postRepository.findByProfileIdAndVisibilityOrderByCreatedAtDesc(
                                userId, Post.PostVisibility.PUBLIC, pageable);
                }
        }

        private List<FeedItemResponseDto> convertPostsToFeedItems(Page<Post> posts) {
                return posts.getContent().stream()
                        .map(dtoConversionService::convertToFeedItemResponseDto)
                        .collect(Collectors.toList());
        }
}
