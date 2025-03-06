package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.post.PostDto;
import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.PostNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.PostService;
import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
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
    private final AuthUtils authUtils;

    @Autowired
    public PostController(PostService postService, AuthUtils authUtils) {
        this.postService = postService;
        this.authUtils = authUtils;
    }

    private User getCurrentUser() {
        return authUtils.getCurrentUserOrElseThrow();
    }

    private <T> ResponseEntity<ApiResponse<T>> createApiResponse(String message, T data, HttpStatus status) {
        ApiResponse<T> response = new ApiResponse<>(true, message, data);
        return ResponseEntity.status(status).body(response);
    }

    private ResponseEntity<ApiResponse<String>> createApiResponse(String message, HttpStatus status) {
        ApiResponse<String> response = new ApiResponse<>(true, message, null);
        return ResponseEntity.status(status).body(response);
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
        return createApiResponse("Post created successfully", createdPost, HttpStatus.CREATED);
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
        return createApiResponse("Post retrieved successfully", post, HttpStatus.OK);
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
        return createApiResponse("Post deleted successfully", HttpStatus.OK);
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
        return createApiResponse("Post updated successfully", updatedPost, HttpStatus.OK);
    }

    /**
     * Toggles like status on a specific post (like if not liked, unlike if already liked).
     *
     * @param postId the ID of the post to toggle like status
     * @return ResponseEntity with a success or failure message
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<String>> toggleLikePost(@PathVariable Long postId) {
        User user = getCurrentUser();
        logger.info("User {} is toggling like status on post with ID {}.", user.getUsername(), postId);

        boolean toggled = postService.toggleLikePost(postId, user.getProfile().getId());
        String message = toggled ? "Post liked successfully" : "Post unliked successfully";
        String failureMessage = toggled ? "Post already liked" : "Post was not liked";

        return createApiResponse(toggled ? message : failureMessage, HttpStatus.OK);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handlePostNotFoundException(PostNotFoundException ex) {
        ApiResponse<String> response = new ApiResponse<>(false, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
