package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.CommentDto;
import com.yalice.wardrobe_social_app.dtos.PostDto;
import com.yalice.wardrobe_social_app.entities.Comment;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.FeedService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller responsible for handling feed-related operations.
 * Provides endpoints for posts, comments, and likes management.
 */
@RestController
@RequestMapping("/api/feed")
public class FeedController {

    private final FeedService feedService;
    private final UserService userService;

    /**
     * Constructor for FeedController.
     *
     * @param feedService Service for feed-related operations
     * @param userService Service for user-related operations
     */
    @Autowired
    public FeedController(FeedService feedService, UserService userService) {
        this.feedService = feedService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getUserFeed(Pageable pageable) {
        Optional<User> currentUserOptional = getCurrentUser();
        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long currentUserId = currentUserOptional.get().getId();
        Page<Post> feed = feedService.getUserFeed(currentUserId, pageable);
        return ResponseEntity.ok(feed);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserPosts(@PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Optional<User> currentUserOptional = getCurrentUser();
        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long currentUserId = currentUserOptional.get().getId();
        Page<Post> posts = feedService.getUserPosts(userId, currentUserId, pageable);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/post")
    public ResponseEntity<?> createPost(@RequestBody PostDto postDto) {
        Optional<User> currentUserOptional = getCurrentUser();
        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long currentUserId = currentUserOptional.get().getId();
        Post post = feedService.createPost(currentUserId, postDto.getContent(), postDto.getOutfitId(),
                postDto.getVisibility());
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        Optional<User> currentUserOptional = getCurrentUser();
        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long currentUserId = currentUserOptional.get().getId();
        feedService.deletePost(postId, currentUserId);
        return ResponseEntity.ok("Post deleted");
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId) {
        Optional<User> currentUserOptional = getCurrentUser();
        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long currentUserId = currentUserOptional.get().getId();
        boolean liked = feedService.likePost(postId, currentUserId);
        return ResponseEntity.ok(liked ? "Post liked" : "Post already liked");
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId) {
        Optional<User> currentUserOptional = getCurrentUser();
        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long currentUserId = currentUserOptional.get().getId();
        boolean unliked = feedService.unlikePost(postId, currentUserId);
        return ResponseEntity.ok(unliked ? "Post unliked" : "Post was not liked");
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody CommentDto commentDto) {
        Optional<User> currentUserOptional = getCurrentUser();
        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long currentUserId = currentUserOptional.get().getId();
        Comment comment = feedService.addComment(postId, currentUserId, commentDto.getContent());
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        Optional<User> currentUserOptional = getCurrentUser();
        if (currentUserOptional.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Long currentUserId = currentUserOptional.get().getId();
        feedService.deleteComment(commentId, currentUserId);
        return ResponseEntity.ok("Comment deleted");
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getPostComments(@PathVariable Long postId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<Comment> comments = feedService.getPostComments(postId, pageable);
        return ResponseEntity.ok(comments);
    }

    /**
     * Utility method to get the current authenticated user
     */
    private Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        String username = authentication.getName();
        return userService.findUserByUsername(username);
    }
}