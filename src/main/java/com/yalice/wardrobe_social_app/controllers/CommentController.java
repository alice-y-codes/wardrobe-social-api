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
        logger.info("Attempting to create new comment on post ID: {}", postId);

        User currentUser = getLoggedInUser();
        try {
            CommentResponseDto createdComment = commentService.createComment(currentUser.getId(), postId, commentDto);
            logger.info("Successfully created comment with ID: {} on post ID: {} by user ID: {}",
                    createdComment.getId(), postId, currentUser.getId());
            return createSuccessResponse("Comment created successfully", createdComment);
        } catch (Exception e) {
            logger.error("Failed to create comment on post ID: {} by user ID: {}", postId, currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to create comment");
        }
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
        logger.info("Attempting to update comment with ID: {}", commentId);

        User currentUser = getLoggedInUser();
        try {
            CommentResponseDto updatedComment = commentService.updateComment(currentUser.getId(), commentId,
                    commentDto);
            logger.info("Successfully updated comment with ID: {} by user ID: {}", commentId, currentUser.getId());
            return createSuccessResponse("Comment updated successfully", updatedComment);
        } catch (Exception e) {
            logger.error("Failed to update comment with ID: {} by user ID: {}", commentId, currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to update comment");
        }
    }

    /**
     * Deletes a comment.
     *
     * @param commentId the ID of the comment to delete
     * @return ResponseEntity with a success message
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long commentId) {
        logger.info("Attempting to delete comment with ID: {}", commentId);

        User currentUser = getLoggedInUser();
        try {
            commentService.deleteComment(currentUser.getId(), commentId);
            logger.info("Successfully deleted comment with ID: {} by user ID: {}", commentId, currentUser.getId());
            return createSuccessResponse("Comment deleted successfully", null);
        } catch (Exception e) {
            logger.error("Failed to delete comment with ID: {} by user ID: {}", commentId, currentUser.getId(), e);
            return createInternalServerErrorResponse("Failed to delete comment");
        }
    }

    /**
     * Gets all comments for a specific post.
     *
     * @param postId the ID of the post
     * @return ResponseEntity containing the list of comments
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<List<CommentResponseDto>>> getPostComments(@PathVariable Long postId) {
        logger.info("Retrieving comments for post ID: {}", postId);

        try {
            List<CommentResponseDto> comments = commentService.getPostComments(postId);
            logger.info("Successfully retrieved {} comments for post ID: {}", comments.size(), postId);
            return createSuccessResponse("Comments retrieved successfully", comments);
        } catch (Exception e) {
            logger.error("Failed to retrieve comments for post ID: {}", postId, e);
            return createInternalServerErrorResponse("Failed to retrieve comments");
        }
    }

    /**
     * Gets a specific comment by ID.
     *
     * @param commentId the ID of the comment to retrieve
     * @return ResponseEntity containing the comment
     */
    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponseDto>> getComment(@PathVariable Long commentId) {
        logger.info("Retrieving comment with ID: {}", commentId);

        try {
            CommentResponseDto comment = commentService.getComment(commentId);
            logger.info("Successfully retrieved comment with ID: {}", commentId);
            return createSuccessResponse("Comment retrieved successfully", comment);
        } catch (Exception e) {
            logger.error("Failed to retrieve comment with ID: {}", commentId, e);
            return createNotFoundResponse("Comment not found with ID: " + commentId);
        }
    }
}
