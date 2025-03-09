package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.controllers.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.dtos.post.PostDto;
import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling post-related operations.
 */
@RestController
@RequestMapping("/api/feed")
public class PostController extends ApiBaseController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService, AuthUtils authUtils) {
        super(authUtils);
        this.postService = postService;
    }

    /**
     * Creates a new post.
     */
    @PostMapping("/post")
    public ResponseEntity<ApiResponse<PostResponseDto>> createPost(@RequestBody PostDto postDto) {
        return handleEntityAction(() -> {
            User user = getLoggedInUser();
            logger.info("User {} is creating a new post.", user.getUsername());
            return postService.createPost(user.getId(), postDto);
        }, "create", "Post", "created");
    }

    /**
     * Retrieves a specific post by ID.
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPost(@PathVariable Long postId) {
        return handleEntityRetrieval(() -> {
            User user = getLoggedInUser();
            logger.info("User {} is requesting post with ID {}.", user.getUsername(), postId);
            return postService.getPost(postId, user.getId());
        }, "Post");
    }

    /**
     * Updates a post.
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> updatePost(@PathVariable Long postId, @RequestBody PostDto postDto) {
        return handleEntityAction(() -> {
            User user = getLoggedInUser();
            logger.info("User {} is updating post with ID {}.", user.getUsername(), postId);
            return postService.updatePost(postId, user.getId(), postDto);
        }, "update", "Post", "updated");
    }

    /**
     * Deletes a post.
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long postId) {
        return handleVoidAction(() -> {
            User user = getLoggedInUser();
            logger.info("User {} is deleting post with ID {}.", user.getUsername(), postId);
            postService.deletePost(postId, user.getId());
        }, "delete", "Post", "deleted");
    }

    /**
     * Toggles like status on a post.
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<String>> toggleLikePost(@PathVariable Long postId) {
        return handleEntityAction(() -> {
            User user = getLoggedInUser();
            logger.info("User {} is toggling like on post with ID {}.", user.getUsername(), postId);
            boolean toggled = postService.toggleLikePost(postId, user.getProfile().getId());
            return toggled ? "Post liked successfully" : "Post unliked successfully";
        }, "toggle like", "Post", "like toggled");
    }
}
