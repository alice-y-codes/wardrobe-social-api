package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.dtos.CommentDto;
import com.yalice.wardrobe_social_app.entities.Comment;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.CommentService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.utilities.CurrentUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling comment-related operations.
 * Provides endpoints for adding, deleting, and fetching comments.
 */
@RestController
@RequestMapping("/api/feed")
public class CommentController {

    private final CommentService commentService;
    private final CurrentUser currentUser;

    /**
     * Constructor for CommentController.
     *
     * @param commentService Service for comment-related operations
     * @param userSearchService Service for user-related operations
     */
    @Autowired
    public CommentController(CommentService commentService, UserSearchService userSearchService) {
        this.commentService = commentService;
        this.currentUser = new CurrentUser(userSearchService);
    }

    /**
     * Adds a comment to a specific post.
     *
     * @param postId the ID of the post to comment on
     * @param commentDto the content of the comment
     * @return ResponseEntity containing the added comment
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody CommentDto commentDto) {
        // Get the current authenticated user or throw UnauthorizedAccessException
        User user = currentUser.getCurrentUserOrElseThrow();

        Long currentUserId = user.getId();
        Comment comment = commentService.addComment(postId, currentUserId, commentDto.getContent());
        return ResponseEntity.ok(comment);
    }

    /**
     * Deletes a specific comment from a post.
     *
     * @param postId the ID of the post from which the comment is being deleted
     * @param commentId the ID of the comment to be deleted
     * @return ResponseEntity with a success message
     */
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        // Get the current authenticated user or throw UnauthorizedAccessException
        User user = currentUser.getCurrentUserOrElseThrow();

        Long currentUserId = user.getId();
        commentService.deleteComment(commentId, currentUserId);
        return ResponseEntity.ok("Comment deleted");
    }

    /**
     * Retrieves all comments for a specific post.
     *
     * @param postId the ID of the post
     * @param pageable the pagination information
     * @return ResponseEntity containing a page of comments for the post
     */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getPostComments(@PathVariable Long postId,
                                             @PageableDefault(size = 20) Pageable pageable) {
        Page<Comment> comments = commentService.getPostComments(postId, pageable);
        return ResponseEntity.ok(comments);
    }
}
