package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.post.PostDto;
import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.PostNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.PostService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.utilities.CurrentUser;
import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller responsible for handling post-related operations.
 * Provides endpoints for creating, deleting, liking, and unliking posts.
 */
@RestController
@RequestMapping("/api/feed")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    private final PostService postService;
    private final CurrentUser currentUser;

    @Autowired
    public PostController(PostService postService, UserSearchService userSearchService) {
        this.postService = postService;
        this.currentUser = new CurrentUser(userSearchService);
    }

    private User getCurrentUser() {
        return currentUser.getCurrentUserOrElseThrow();
    }

    /**
     * Creates a new post.
     *
     * @param postDto the details of the post to create
     * @return ResponseEntity containing the created post
     */
    @PostMapping("/post")
    public ResponseEntity<ApiResponse<PostResponseDto>> createPost(@RequestBody PostDto postDto) {
        User user = getCurrentUser();
        logger.info("User {} is creating a new post.", user.getUsername());
        PostResponseDto createdPost = postService.createPost(user.getId(), postDto);
        ApiResponse<PostResponseDto> response = new ApiResponse<>(true, "Post created successfully", createdPost);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get a specific post by its ID.
     *
     * @param postId the ID of the post to get
     * @return ResponseEntity with the post
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPost(@PathVariable Long postId) {
        User user = getCurrentUser();
        logger.info("User {} is requesting post with ID {}.", user.getUsername(), postId);
        PostResponseDto post = postService.getPost(postId, user.getId());
        ApiResponse<PostResponseDto> response = new ApiResponse<>(true, "Post retrieved successfully", post);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a specific post by its ID.
     *
     * @param postId the ID of the post to delete
     * @return ResponseEntity with a success message
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> deletePost(@PathVariable Long postId) {
        User user = getCurrentUser();
        logger.info("User {} is deleting post with ID {}.", user.getUsername(), postId);
        postService.deletePost(postId, user.getId());
        ApiResponse<String> response = new ApiResponse<>(true, "Post deleted successfully", "Post deleted");
        return ResponseEntity.ok(response);
    }

    /**
     * Update a specific post by its ID.
     *
     * @param postId the ID of the post to update
     * @param postDto the updated post details
     * @return ResponseEntity with the updated post
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> updatePost(@PathVariable Long postId, @RequestBody PostDto postDto) {
        User user = getCurrentUser();
        logger.info("User {} is updating post with ID {}.", user.getUsername(), postId);
        PostResponseDto updatedPost = postService.updatePost(postId, user.getId(), postDto);
        ApiResponse<PostResponseDto> response = new ApiResponse<>(true, "Post updated successfully", updatedPost);
        return ResponseEntity.ok(response);
    }

    /**
     * Likes a specific post.
     *
     * @param postId the ID of the post to like
     * @return ResponseEntity with a success or failure message
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<String>> likePost(@PathVariable Long postId) {
        User user = getCurrentUser();
        logger.info("User {} is liking post with ID {}.", user.getUsername(), postId);
        boolean liked = postService.likePost(postId, user.getId());
        ApiResponse<String> response = new ApiResponse<>(true, liked ? "Post liked successfully" : "Post already liked", liked ? "Post liked" : "Post already liked");
        return ResponseEntity.ok(response);
    }

    /**
     * Unlikes a specific post.
     *
     * @param postId the ID of the post to unlike
     * @return ResponseEntity with a success or failure message
     */
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<String>> unlikePost(@PathVariable Long postId) {
        User user = getCurrentUser();
        logger.info("User {} is unliking post with ID {}.", user.getUsername(), postId);
        boolean unliked = postService.unlikePost(postId, user.getId());
        ApiResponse<String> response = new ApiResponse<>(true, unliked ? "Post unliked successfully" : "Post was not liked", unliked ? "Post unliked" : "Post was not liked");
        return ResponseEntity.ok(response);
    }

    // Global exception handler for PostNotFoundException
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handlePostNotFoundException(PostNotFoundException ex) {
        ApiResponse<String> response = new ApiResponse<>(false, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
