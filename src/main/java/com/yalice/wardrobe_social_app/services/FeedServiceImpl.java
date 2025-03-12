package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.feed.FeedItemResponseDto;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendResponseDto;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.FeedService;
import com.yalice.wardrobe_social_app.interfaces.FriendService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.mappers.FeedItemMapper;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl extends BaseService implements FeedService {

        private final PostRepository postRepository;
        private final FriendService friendService;
        private final UserSearchService userSearchService;
        private final FeedItemMapper feedItemMapper;

        @Override
        public List<FeedItemResponseDto> getFeed(Long userId, int page, int size) {
                log.info("Fetching feed for userId={} (page={}, size={})", userId, page, size);

                List<Long> friendIds = getFriendIds(userId);
                Page<Post> posts = postRepository.findByProfileIdInOrderByCreatedAtDesc(
                        friendIds, Pageable.ofSize(size).withPage(page)
                );

                return mapToFeedResponse(posts);
        }

        @Override
        public List<FeedItemResponseDto> getFeedBySeason(Long userId, String season, int page, int size) {
                log.info("Fetching seasonal feed for userId={} (season={}, page={}, size={})", userId, season, page, size);

                List<Long> friendIds = getFriendIds(userId);
                Page<Post> posts = postRepository.findByProfileIdInAndOutfitSeasonOrderByCreatedAtDesc(
                        friendIds, season, Pageable.ofSize(size).withPage(page)
                );

                return mapToFeedResponse(posts);
        }

        @Override
        public List<FeedItemResponseDto> getFeedByCategory(Long userId, String category, int page, int size) {
                log.info("Fetching category feed for userId={} (category={}, page={}, size={})", userId, category, page, size);

                List<Long> friendIds = getFriendIds(userId);
                Page<Post> posts = postRepository.findByProfileIdInAndOutfitCategoryOrderByCreatedAtDesc(
                        friendIds, category, Pageable.ofSize(size).withPage(page)
                );

                return mapToFeedResponse(posts);
        }

        @Override
        public Page<Post> getUserPosts(Long userId, Long viewerId, Pageable pageable) {
                log.info("Fetching user posts for userId={} viewed by userId={}", userId, viewerId);

                User user = userSearchService.getUserEntityById(userId);

                if (user == null) {
                        throw new ResourceNotFoundException("User not found with id: " + userId);
                }

                boolean isViewerFriend = userId.equals(viewerId) || friendService.areFriends(userId, viewerId);
                List<Post.PostVisibility> visibility = isViewerFriend
                        ? List.of(Post.PostVisibility.PUBLIC, Post.PostVisibility.FRIENDS_ONLY)
                        : List.of(Post.PostVisibility.PUBLIC);

                return postRepository.findByProfileIdAndVisibilityInOrderByCreatedAtDesc(userId, visibility, pageable);
        }

        private List<Long> getFriendIds(Long userId) {
                List<Long> friendIds = friendService.getFriends(userId).stream()
                        .map(FriendResponseDto::getUserId)
                        .collect(Collectors.toList());
                friendIds.add(userId);
                return friendIds;
        }

        private List<FeedItemResponseDto> mapToFeedResponse(Page<Post> posts) {
                return posts.stream()
                        .map(feedItemMapper::toResponseDto)
                        .collect(Collectors.toList());
        }
}
