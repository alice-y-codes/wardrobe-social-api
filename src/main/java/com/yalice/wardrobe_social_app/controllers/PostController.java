package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.PostDto;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.PostService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.utilities.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling post-related operations.
 * Provides endpoints for creating, deleting, liking, and unliking posts.
 */
@RestController
@RequestMapping("/api/feed")
public class PostController {

    private final PostService postService;
    private final CurrentUser currentUser;

    /**
     * Constructor for PostController.
     *
     * @param postService Service for post-related operations
     * @param userService Service for user-related operations
     */
    @Autowired
    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.currentUser = new CurrentUser(userService);
    }

    /**
     * Creates a new post.
     *
     * @param postDto the details of the post to create
     * @return ResponseEntity containing the created post
     */
    @PostMapping("/post")
    public ResponseEntity<?> createPost(@RequestBody PostDto postDto) {
        User user = currentUser.getCurrentUserOrElseThrow();

        Long currentUserId = user.getId();
        Post post = postService.createPost(currentUserId, postDto.getContent(), postDto.getOutfitId(), postDto.getVisibility());
        return ResponseEntity.ok(post);
    }

    /**
     * Deletes a specific post by its ID.
     *
     * @param postId the ID of the post to delete
     * @return ResponseEntity with a success message
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        // Get the current authenticated user or throw UnauthorizedAccessException
        User user = currentUser.getCurrentUserOrElseThrow();

        Long currentUserId = user.getId();
        postService.deletePost(postId, currentUserId);
        return ResponseEntity.ok("Post deleted");
    }

    /* TODO - updatePost */

    /**
     * Likes a specific post.
     *
     * @param postId the ID of the post to like
     * @return ResponseEntity with a success or failure message
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId) {
        // Get the current authenticated user or throw UnauthorizedAccessException
        User user = currentUser.getCurrentUserOrElseThrow();

        Long currentUserId = user.getId();
        boolean liked = postService.likePost(postId, currentUserId);
        return ResponseEntity.ok(liked ? "Post liked" : "Post already liked");
    }

    /**
     * Unlikes a specific post.
     *
     * @param postId the ID of the post to unlike
     * @return ResponseEntity with a success or failure message
     */
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<?> unlikePost(@PathVariable Long postId) {
        // Get the current authenticated user or throw UnauthorizedAccessException
        User user = currentUser.getCurrentUserOrElseThrow();

        Long currentUserId = user.getId();
        boolean unliked = postService.unlikePost(postId, currentUserId);
        return ResponseEntity.ok(unliked ? "Post unliked" : "Post was not liked");
    }
}