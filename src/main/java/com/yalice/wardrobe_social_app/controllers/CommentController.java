package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.comment.CommentDto;
import com.yalice.wardrobe_social_app.dtos.comment.CommentResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.CommentService;
import com.yalice.wardrobe_social_app.utilities.ApiResponse;
import com.yalice.wardrobe_social_app.utilities.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for handling comment-related operations.
 * Provides endpoints for creating, updating, and managing comments.
 */
@RestController
@RequestMapping("/api/comments")
public class CommentController extends ApiBaseController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService, AuthUtils authUtils) {
        super(authUtils);
        this.commentService = commentService;
    }

    /**
     * Creates a new comment.
     *
     * @param postId     the ID of the post to comment on
     * @param commentDto the comment data
     * @return ResponseEntity containing the created comment
     */
    @PostMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
            @PathVariable Long postId,
            @RequestBody CommentDto commentDto) {
        return handleCommentOperation(() -> {
            User currentUser = getLoggedInUser();
            return commentService.createComment(currentUser.getId(), postId, commentDto);
        }, "create", postId);
    }

    /**
     * Updates an existing comment.
     *
     * @param commentId  the ID of the comment to update
     * @param commentDto the updated comment data
     * @return ResponseEntity containing the updated comment
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentDto commentDto) {
        return handleCommentOperation(() -> {
            User currentUser = getLoggedInUser();
            return commentService.updateComment(currentUser.getId(), commentId, commentDto);
        }, "update", commentId);
    }

    /**
     * Deletes a comment.
     *
     * @param commentId the ID of the comment to delete
     * @return ResponseEntity with a success message
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId) {
        return handleCommentDeletion(commentId);
    }

    /**
     * Gets all comments for a specific post.
     *
     * @param postId the ID of the post
     * @return ResponseEntity containing the list of comments
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<List<CommentResponseDto>>> getPostComments(@PathVariable Long postId) {
        return handleCommentRetrieval(() -> commentService.getPostComments(postId), "retrieve comments for post", postId);
    }

    /**
     * Gets a specific comment by ID.
     *
     * @param commentId the ID of the comment to retrieve
     * @return ResponseEntity containing the comment
     */
    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> getComment(@PathVariable Long commentId) {
        return handleCommentRetrieval(() -> commentService.getComment(commentId), "retrieve comment", commentId);
    }

    // Helper method for handling comment creation, update, and deletion
    private ResponseEntity<ApiResponse<CommentResponseDto>> handleCommentOperation(
            CommentAction commentAction, String operation, Long id) {
        try {
            CommentResponseDto comment = commentAction.execute();
            logger.info("Successfully {} comment with ID: {}", operation, id);
            return createSuccessResponse("Comment " + operation + "d successfully", comment);
        } catch (Exception e) {
            logger.error("Failed to {} comment with ID: {}", operation, id, e);
            return createInternalServerErrorResponse("Failed to " + operation + " comment");
        }
    }

    // Helper method for handling comment retrieval
    private ResponseEntity<ApiResponse<CommentResponseDto>> handleCommentRetrieval(
            CommentRetriever commentRetriever, String operation, Long id) {
        try {
            CommentResponseDto comment = commentRetriever.execute();
            logger.info("Successfully retrieved comment with ID: {}", id);
            return createSuccessResponse("Comment " + operation + "d successfully", comment);
        } catch (Exception e) {
            logger.error("Failed to {} comment with ID: {}", operation, id, e);
            return createNotFoundResponse("Comment not found with ID: " + id);
        }
    }

    // Helper method for handling comment deletion
    private ResponseEntity<ApiResponse<Void>> handleCommentDeletion(Long commentId) {
        try {
            User currentUser = getLoggedInUser();
            commentService.deleteComment(currentUser.getId(), commentId);
            logger.info("Successfully deleted comment with ID: {}", commentId);
            return createSuccessResponse("Comment deleted successfully", null);
        } catch (Exception e) {
            logger.error("Failed to delete comment with ID: {}", commentId, e);
            return createInternalServerErrorResponse("Failed to delete comment");
        }
    }

    // Functional interfaces for generic handling
    @FunctionalInterface
    interface CommentAction {
        CommentResponseDto execute() throws Exception;
    }

    @FunctionalInterface
    interface CommentRetriever {
        CommentResponseDto execute() throws Exception;
    }
}
