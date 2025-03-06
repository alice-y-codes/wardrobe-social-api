package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.feed.FeedItemDto;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendResponseDto;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.enums.PostVisibility;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.FeedService;
import com.yalice.wardrobe_social_app.interfaces.FriendService;
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
        private final FriendService friendService;
        private final UserSearchService userSearchService;

        @Autowired
        public FeedServiceImpl(PostRepository postRepository, FriendService friendService,
                        UserSearchService userSearchService) {
                this.postRepository = postRepository;
                this.friendService = friendService;
                this.userSearchService = userSearchService;
        }

        @Override
        public List<FeedItemDto> getFeed(Long userId, int page, int size) {
                logger.info("Retrieving feed for user ID: {} (page: {}, size: {})", userId, page, size);

                List<FriendResponseDto> friendships = friendService.getFriends(userId);
                List<Long> friendIds = friendships.stream()
                                .map(FriendResponseDto::getUserId)
                                .collect(Collectors.toList());
                friendIds.add(userId);

                Page<Post> posts = postRepository.findFeedPostsForUser(friendIds, Pageable.ofSize(size).withPage(page));
                return posts.getContent().stream()
                                .map(this::convertToFeedItemDto)
                                .collect(Collectors.toList());
        }

        @Override
        public List<FeedItemDto> getFeedBySeason(Long userId, String season, int page, int size) {
                logger.info("Retrieving feed filtered by season '{}' for user ID: {} (page: {}, size: {})",
                                season, userId, page, size);

                List<FriendResponseDto> friendships = friendService.getFriends(userId);
                List<Long> friendIds = friendships.stream()
                                .map(FriendResponseDto::getUserId)
                                .collect(Collectors.toList());
                friendIds.add(userId);

                Page<Post> posts = postRepository.findFeedPostsForUserBySeason(friendIds, season,
                                Pageable.ofSize(size).withPage(page));
                return posts.getContent().stream()
                                .map(this::convertToFeedItemDto)
                                .collect(Collectors.toList());
        }

        @Override
        public List<FeedItemDto> getFeedByCategory(Long userId, String category, int page, int size) {
                logger.info("Retrieving feed filtered by category '{}' for user ID: {} (page: {}, size: {})",
                                category, userId, page, size);

                List<FriendResponseDto> friendships = friendService.getFriends(userId);
                List<Long> friendIds = friendships.stream()
                                .map(FriendResponseDto::getUserId)
                                .collect(Collectors.toList());
                friendIds.add(userId);

                Page<Post> posts = postRepository.findFeedPostsForUserByCategory(friendIds, category,
                                Pageable.ofSize(size).withPage(page));
                return posts.getContent().stream()
                                .map(this::convertToFeedItemDto)
                                .collect(Collectors.toList());
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
                        return postRepository.findByUserIdAndVisibilityInOrderByCreatedAtDesc(
                                        userId, List.of(PostVisibility.PUBLIC, PostVisibility.FRIENDS_ONLY), pageable);
                } else {
                        return postRepository.findByUserIdAndVisibilityOrderByCreatedAtDesc(
                                        userId, PostVisibility.PUBLIC, pageable);
                }
        }

        private FeedItemDto convertToFeedItemDto(Post post) {
                FeedItemDto dto = new FeedItemDto();
                dto.setId(post.getId());
                dto.setType("POST");
                dto.setUser(convertToUserResponseDto(post.getProfile().getUser()));
                dto.setCreatedAt(post.getCreatedAt());
                dto.setUpdatedAt(post.getUpdatedAt());
                dto.setSeason(post.getOutfit().getSeason());
                dto.setCategory(post.getOutfit().getCategory());
                dto.setLikesCount(post.getLikes().size());
                dto.setCommentsCount(post.getComments().size());
                // TODO: Implement isLikedByCurrentUser logic
                dto.setLikedByCurrentUser(false);
                return dto;
        }
}
