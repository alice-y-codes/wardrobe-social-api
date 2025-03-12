package com.yalice.wardrobe_social_app.services.social;

import com.yalice.wardrobe_social_app.dtos.feed.FeedItemResponseDto;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendResponseDto;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.FeedService;
import com.yalice.wardrobe_social_app.interfaces.FriendService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.mappers.FeedItemMapper;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedServiceImpl extends BaseService<Post, Long> implements FeedService {

        private final PostRepository postRepository;
        private final FriendService friendService;
        private final UserSearchService userSearchService;
        private final FeedItemMapper feedItemMapper;

        private static final int MAX_PAGE_SIZE = 50;

        public FeedServiceImpl(
                        PostRepository postRepository,
                        FriendService friendService,
                        UserSearchService userSearchService,
                        FeedItemMapper feedItemMapper) {
                this.postRepository = postRepository;
                this.friendService = friendService;
                this.userSearchService = userSearchService;
                this.feedItemMapper = feedItemMapper;
        }

        @Override
        protected JpaRepository<Post, Long> getRepository() {
                return postRepository;
        }

        @Override
        protected String getEntityName() {
                return "Post";
        }

        @Override
        @Transactional(readOnly = true)
        public List<FeedItemResponseDto> getFeed(Long userId, int page, int size) {
                logger.info("Fetching feed for userId={} (page={}, size={})", userId, page, size);
                validateFeedParameters(userId, page, size);

                List<Long> friendIds = getFriendIds(userId);
                Page<Post> posts = postRepository.findByProfileIdInOrderByCreatedAtDesc(
                                friendIds,
                                createPageRequest(page, size));

                return mapEntityList(posts.getContent(), feedItemMapper::toResponseDto);
        }

        @Override
        @Transactional(readOnly = true)
        public List<FeedItemResponseDto> getFeedBySeason(Long userId, String season, int page, int size) {
                logger.info("Fetching seasonal feed for userId={} (season={}, page={}, size={})",
                                userId, season, page, size);

                validateFeedParameters(userId, page, size);
                validationService.validateStringNotEmpty(season, "Season");

                List<Long> friendIds = getFriendIds(userId);
                Page<Post> posts = postRepository.findByProfileIdInAndOutfitSeasonOrderByCreatedAtDesc(
                                friendIds,
                                season,
                                createPageRequest(page, size));

                return mapEntityList(posts.getContent(), feedItemMapper::toResponseDto);
        }

        @Override
        @Transactional(readOnly = true)
        public List<FeedItemResponseDto> getFeedByCategory(Long userId, String category, int page, int size) {
                logger.info("Fetching category feed for userId={} (category={}, page={}, size={})",
                                userId, category, page, size);

                validateFeedParameters(userId, page, size);
                validationService.validateStringNotEmpty(category, "Category");

                List<Long> friendIds = getFriendIds(userId);
                Page<Post> posts = postRepository.findByProfileIdInAndOutfitCategoryOrderByCreatedAtDesc(
                                friendIds,
                                category,
                                createPageRequest(page, size));

                return mapEntityList(posts.getContent(), feedItemMapper::toResponseDto);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<Post> getUserPosts(Long userId, Long viewerId, Pageable pageable) {
                logger.info("Fetching user posts for userId={} viewed by userId={}", userId, viewerId);

                validateUserPostParameters(userId, viewerId, pageable);
                User user = validateAndGetUser(userId);

                List<Post.PostVisibility> visibility = determinePostVisibility(userId, viewerId);

                return postRepository.findByProfileIdAndVisibilityInOrderByCreatedAtDesc(
                                userId,
                                visibility,
                                pageable);
        }

        private List<Long> getFriendIds(Long userId) {
                List<Long> friendIds = friendService.getFriends(userId).stream()
                                .map(FriendResponseDto::getUserId)
                                .collect(Collectors.toList());
                friendIds.add(userId); // Include user's own posts
                return friendIds;
        }

        private void validateFeedParameters(Long userId, int page, int size) {
                validationService.validateNotNull(userId, "User ID");
                validationService.validatePositive((long) page, "Page number");
                validationService.validatePositive((long) size, "Page size");
                validationService.validateExists(size <= MAX_PAGE_SIZE,
                                String.format("Page size must not exceed %d", MAX_PAGE_SIZE));
        }

        private void validateUserPostParameters(Long userId, Long viewerId, Pageable pageable) {
                validationService.validateNotNull(userId, "User ID");
                validationService.validateNotNull(viewerId, "Viewer ID");
                validationService.validateNotNull(pageable, "Pageable");
                validationService.validateExists(pageable.getPageSize() <= MAX_PAGE_SIZE,
                                String.format("Page size must not exceed %d", MAX_PAGE_SIZE));
        }

        private User validateAndGetUser(Long userId) {
                User user = userSearchService.getUserEntityById(userId);
                validationService.validateNotNull(user, "User");
                return user;
        }

        private List<Post.PostVisibility> determinePostVisibility(Long userId, Long viewerId) {
                boolean isViewerFriend = userId.equals(viewerId) || friendService.areFriends(userId, viewerId);
                return isViewerFriend
                                ? List.of(Post.PostVisibility.PUBLIC, Post.PostVisibility.FRIENDS_ONLY)
                                : List.of(Post.PostVisibility.PUBLIC);
        }

        private Pageable createPageRequest(int page, int size) {
                return PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));
        }
}
