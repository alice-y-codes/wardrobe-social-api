package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.controllers.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import com.yalice.wardrobe_social_app.dtos.post.PostDto;
import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.PostService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller responsible for handling post-related operations.
 */
@RestController
@RequestMapping("/api/feed")
public class PostController {

    private final PostService postService;
    private final AuthUtils authUtils;

    public PostController(PostService postService, AuthUtils authUtils) {
        this.postService = postService;
        this.authUtils = authUtils;
    }

    /**
     * Creates a new post.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponseDto> createPost(
            @RequestPart("post") PostDto postDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        User currentUser = authUtils.getCurrentUserOrElseThrow();
        PostResponseDto post = postService.createPost(currentUser.getProfile().getId(), postDto, image);
        return ApiResponse.<PostResponseDto>builder()
                .success(true)
                .data(post)
                .build();
    }

    /**
     * Retrieves a specific post by ID.
     */
    @GetMapping("/{postId}")
    public ApiResponse<PostResponseDto> getPost(@PathVariable Long postId) {
        User currentUser = authUtils.getCurrentUserOrElseThrow();
        PostResponseDto post = postService.getPost(postId, currentUser.getProfile().getId());
        return ApiResponse.<PostResponseDto>builder()
                .success(true)
                .data(post)
                .build();
    }

    /**
     * Updates a post.
     */
    @PatchMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponseDto> updatePost(
            @PathVariable Long postId,
            @RequestPart("post") PostDto postDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        User currentUser = authUtils.getCurrentUserOrElseThrow();
        PostResponseDto post = postService.updatePost(postId, currentUser.getProfile().getId(), postDto, image);
        return ApiResponse.<PostResponseDto>builder()
                .success(true)
                .data(post)
                .build();
    }

    /**
     * Deletes a post.
     */
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(@PathVariable Long postId) {
        User currentUser = authUtils.getCurrentUserOrElseThrow();
        postService.deletePost(postId, currentUser.getProfile().getId());
        return ApiResponse.<Void>builder()
                .success(true)
                .build();
    }

    /**
     * Toggles like status on a post.
     */
    @PostMapping("/{postId}/like")
    public ApiResponse<String> toggleLikePost(@PathVariable Long postId) {
        User currentUser = authUtils.getCurrentUserOrElseThrow();
        boolean liked = postService.toggleLikePost(postId, currentUser.getProfile().getId());
        return ApiResponse.<String>builder()
                .success(true)
                .data(liked ? "Post liked successfully" : "Post unliked successfully")
                .build();
    }
}
