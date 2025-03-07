package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.comment.CommentDto;
import com.yalice.wardrobe_social_app.dtos.comment.CommentResponseDto;
import com.yalice.wardrobe_social_app.interfaces.CommentService;
import com.yalice.wardrobe_social_app.controllers.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for handling comment-related operations.
 * Provides endpoints for creating, updating, retrieving, and deleting comments.
 */
@RestController
@RequestMapping("/comments")
public class CommentController extends ApiBaseController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService, AuthUtils authUtils) {
        super(authUtils);
        this.commentService = commentService;
    }

    /**
     * Creates a new comment for a post.
     */
    @PostMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
            @PathVariable Long postId,
            @RequestBody CommentDto commentDto) {

        return handleEntityAction(() -> commentService.createComment(getLoggedInUser().getId(), postId, commentDto),
                "create", "comment");
    }

    /**
     * Updates an existing comment.
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentDto commentDto) {

        return handleEntityAction(() -> commentService.updateComment(getLoggedInUser().getId(), commentId, commentDto),
                "update", "comment");
    }

    /**
     * Deletes a comment.
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId) {
        return handleEntityAction(() -> {
            commentService.deleteComment(getLoggedInUser().getId(), commentId);
            return null; // No result needed for deletion
        }, "delete", "comment");
    }

    /**
     * Gets all comments for a specific post.
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<List<CommentResponseDto>>> getPostComments(@PathVariable Long postId) {
        return handleEntityAction(() -> commentService.getPostComments(postId), "retrieve", "comments for post");
    }

    /**
     * Gets a specific comment by ID.
     */
    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> getComment(@PathVariable Long commentId) {
        return handleEntityAction(() -> commentService.getComment(commentId), "retrieve", "comment");
    }
}
