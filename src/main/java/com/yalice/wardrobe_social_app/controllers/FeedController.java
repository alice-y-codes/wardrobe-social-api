package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.feed.FeedItemDto;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.FeedService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for handling feed-related operations.
 * Provides endpoints for retrieving and managing the user's feed.
 */
@RestController
@RequestMapping("/api/feed")
public class FeedController extends ApiBaseController {

    private final FeedService feedService;

    /**
     * Constructor for FeedController.
     *
     * @param feedService       Service for feed-related operations
     * @param userSearchService Service for user-related operations
     */
    @Autowired
    public FeedController(FeedService feedService, UserSearchService userSearchService) {
        super(new AuthUtils(userSearchService));
        this.feedService = feedService;
    }

    /**
     * Gets the user's feed with pagination.
     *
     * @param page the page number (zero-based)
     * @param size the number of items per page
     * @return ResponseEntity containing the feed items
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FeedItemDto>>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        logger.info("Retrieving feed for current user (page: {}, size: {})", page, size);

        User currentUser = getLoggedInUser();
        try {
            List<FeedItemDto> feedItems = feedService.getFeed(currentUser.getId(), page, size);
            logger.info("Successfully retrieved {} feed items for user ID: {}", feedItems.size(), currentUser.getId());
            return createSuccessResponse("Feed retrieved successfully", feedItems);
        } catch (Exception e) {
            logger.error("Failed to retrieve feed for user ID: {}", currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to retrieve feed");
        }
    }

    /**
     * Gets the user's feed filtered by season.
     *
     * @param season the season to filter by
     * @param page   the page number (zero-based)
     * @param size   the number of items per page
     * @return ResponseEntity containing the filtered feed items
     */
    @GetMapping("/season/{season}")
    public ResponseEntity<ApiResponse<List<FeedItemDto>>> getFeedBySeason(
            @PathVariable String season,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        logger.info("Retrieving feed filtered by season '{}' for current user (page: {}, size: {})",
                season, page, size);

        User currentUser = getLoggedInUser();
        try {
            List<FeedItemDto> feedItems = feedService.getFeedBySeason(currentUser.getId(), season, page, size);
            logger.info("Successfully retrieved {} feed items for season '{}' and user ID: {}",
                    feedItems.size(), season, currentUser.getId());
            return createSuccessResponse("Feed retrieved successfully", feedItems);
        } catch (Exception e) {
            logger.error("Failed to retrieve feed for season '{}' and user ID: {}",
                    season, currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to retrieve feed");
        }
    }

    /**
     * Gets the user's feed filtered by category.
     *
     * @param category the category to filter by
     * @param page     the page number (zero-based)
     * @param size     the number of items per page
     * @return ResponseEntity containing the filtered feed items
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<FeedItemDto>>> getFeedByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        logger.info("Retrieving feed filtered by category '{}' for current user (page: {}, size: {})",
                category, page, size);

        User currentUser = getLoggedInUser();
        try {
            List<FeedItemDto> feedItems = feedService.getFeedByCategory(currentUser.getId(), category, page, size);
            logger.info("Successfully retrieved {} feed items for category '{}' and user ID: {}",
                    feedItems.size(), category, currentUser.getId());
            return createSuccessResponse("Feed retrieved successfully", feedItems);
        } catch (Exception e) {
            logger.error("Failed to retrieve feed for category '{}' and user ID: {}",
                    category, currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to retrieve feed");
        }
    }

    /**
     * Retrieves the posts of a specific user.
     *
     * @param userId   the ID of the user whose posts are to be fetched
     * @param pageable the pagination information
     * @return ResponseEntity containing the posts of the specified user
     */
    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<ApiResponse<Page<Post>>> getUserPosts(@PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        User currentUser = this.getLoggedInUser();
        Page<Post> posts = feedService.getUserPosts(userId, currentUser.getId(), pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "User posts retrieved successfully", posts));
    }
}
