package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.feed.FeedItemDto;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.FeedService;
import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
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
@RequestMapping("/feed")
public class FeedController extends ApiBaseController {

    private final FeedService feedService;

    @Autowired
    public FeedController(FeedService feedService, AuthUtils authUtils) {
        super(authUtils);
        this.feedService = feedService;
    }

    /**
     * Retrieves the user's feed with pagination.
     *
     * @param page the page number (zero-based)
     * @param size the number of items per page
     * @return ResponseEntity containing the feed items
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FeedItemDto>>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return handleEntityAction(
                () -> feedService.getFeed(getLoggedInUser().getId(), page, size),
                "retrieve feed", "Feed"
        );
    }

    /**
     * Retrieves the user's feed filtered by season with pagination.
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
        return handleEntityAction(
                () -> feedService.getFeedBySeason(getLoggedInUser().getId(), season, page, size),
                "retrieve seasonal feed", "Feed By Season"
        );
    }

    /**
     * Retrieves the user's feed filtered by category with pagination.
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
        return handleEntityAction(
                () -> feedService.getFeedByCategory(getLoggedInUser().getId(), category, page, size),
                "retrieve category feed", "Feed By Category"
        );
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
        User currentUser = getLoggedInUser();
        Page<Post> posts = feedService.getUserPosts(userId, currentUser.getId(), pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "User posts retrieved successfully", posts));
    }
}
