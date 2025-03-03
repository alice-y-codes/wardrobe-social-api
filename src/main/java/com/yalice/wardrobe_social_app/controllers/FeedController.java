package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.FeedService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.utilities.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for handling feed-related operations.
 * Provides endpoints for posts, comments, and likes management.
 */
@RestController
@RequestMapping("/api/feed")
public class FeedController {

    private final FeedService feedService;
    private final CurrentUser currentUser;

    /**
     * Constructor for FeedController.
     *
     * @param feedService Service for feed-related operations
     * @param userService Service for user-related operations
     */
    @Autowired
    public FeedController(FeedService feedService, UserService userService) {
        this.feedService = feedService;
        this.currentUser = new CurrentUser(userService);
    }

    /**
     * Retrieves the feed for the current authenticated user.
     *
     * @param pageable the pagination information
     * @return ResponseEntity containing the feed of the current user
     */
    @GetMapping
    public ResponseEntity<?> getUserFeed(Pageable pageable) {
        User currentUser = this.currentUser.getCurrentUserOrElseThrow();

        Long currentUserId = currentUser.getId();
        Page<Post> feed = feedService.getUserFeed(currentUserId, pageable);
        return ResponseEntity.ok(feed);
    }

    /**
     * Retrieves the posts of a specific user.
     *
     * @param userId the ID of the user whose posts are to be fetched
     * @param pageable the pagination information
     * @return ResponseEntity containing the posts of the specified user
     */
    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<?> getUserPosts(@PathVariable Long userId,
                                          @PageableDefault(size = 20) Pageable pageable) {
        User currentUser = this.currentUser.getCurrentUserOrElseThrow();

        Long currentUserId = currentUser.getId();
        Page<Post> posts = feedService.getUserPosts(userId, currentUserId, pageable);
        return ResponseEntity.ok(posts);
    }
}
