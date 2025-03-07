package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.post.PostDto;
import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.PostService;
import com.yalice.wardrobe_social_app.controllers.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling post-related operations.
 */
@RestController
@RequestMapping("/feed")
public class PostController extends ApiBaseController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService, AuthUtils authUtils) {
        super(authUtils);
        this.postService = postService;
    }

    private User getCurrentUser() {
        return authUtils.getCurrentUserOrElseThrow();
    }

    /**
     * Creates a new post.
     */
    @PostMapping("/post")
    public ResponseEntity<ApiResponse<PostResponseDto>> createPost(@RequestBody PostDto postDto) {
        return handleEntityAction(() -> {
            User user = getCurrentUser();
            logger.info("User {} is creating a new post.", user.getUsername());
            return postService.createPost(user.getId(), postDto);
        }, "Post created successfully", "Post");
    }

    /**
     * Retrieves a specific post by ID.
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPost(@PathVariable Long postId) {
        return handleEntityRetrieval(() -> {
            User user = getCurrentUser();
            logger.info("User {} is requesting post with ID {}.", user.getUsername(), postId);
            return postService.getPost(postId, user.getId());
        }, "Post retrieved successfully");
    }

    /**
     * Updates a post.
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> updatePost(@PathVariable Long postId, @RequestBody PostDto postDto) {
        return handleEntityUpdate(() -> {
            User user = getCurrentUser();
            logger.info("User {} is updating post with ID {}.", user.getUsername(), postId);
            return postService.updatePost(postId, user.getId(), postDto);
        }, "Post updated successfully");
    }

    /**
     * Deletes a post.
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long postId) {
        return handleEntityAction(() -> {
            User user = getCurrentUser();
            logger.info("User {} is deleting post with ID {}.", user.getUsername(), postId);
            postService.deletePost(postId, user.getId());
            return null;
        }, "Post deleted successfully", "Post");
    }

    /**
     * Toggles like status on a post.
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<String>> toggleLikePost(@PathVariable Long postId) {
        return handleEntityAction(() -> {
            User user = getCurrentUser();
            logger.info("User {} is toggling like status on post with ID {}.", user.getUsername(), postId);
            boolean toggled = postService.toggleLikePost(postId, user.getProfile().getId());
            return toggled ? "Post liked successfully" : "Post unliked successfully";
        }, "Like status toggled", "Post");
    }
}
