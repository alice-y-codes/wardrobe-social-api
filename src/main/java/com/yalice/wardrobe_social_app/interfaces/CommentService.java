package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.dtos.comment.CommentDto;
import com.yalice.wardrobe_social_app.dtos.comment.CommentResponseDto;
import java.util.List;

/**
 * Service interface for managing comments.
 */
public interface CommentService {
    /**
     * Creates a new comment.
     *
     * @param userId     the ID of the user creating the comment
     * @param postId     the ID of the post to comment on
     * @param commentDto the comment data
     * @return the created comment
     */
    CommentResponseDto createComment(Long userId, Long postId, CommentDto commentDto);

    /**
     * Updates an existing comment.
     *
     * @param userId     the ID of the user updating the comment
     * @param commentId  the ID of the comment to update
     * @param commentDto the updated comment data
     * @return the updated comment
     */
    CommentResponseDto updateComment(Long userId, Long commentId, CommentDto commentDto);

    /**
     * Deletes a comment.
     *
     * @param userId    the ID of the user deleting the comment
     * @param commentId the ID of the comment to delete
     */
    void deleteComment(Long userId, Long commentId);

    /**
     * Gets all comments for a specific post.
     *
     * @param postId the ID of the post
     * @return the list of comments
     */
    List<CommentResponseDto> getPostComments(Long postId);

    /**
     * Gets a specific comment by ID.
     *
     * @param commentId the ID of the comment to retrieve
     * @return the comment
     */
    CommentResponseDto getComment(Long commentId);
}
