package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.comment.CommentDto;
import com.yalice.wardrobe_social_app.dtos.comment.CommentResponseDto;
import com.yalice.wardrobe_social_app.entities.Comment;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.CommentService;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.repositories.CommentRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends BaseService implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ProfileService profileService;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository,
            ProfileService profileService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.profileService = profileService;
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(Long userId, Long postId, CommentDto commentDto) {
        logger.info("Attempting to create comment on post ID: {} by user ID: {}", postId, userId);

        Profile profile = profileService.getProfileEntityById(userId);
        if (profile == null) {
            throw new ResourceNotFoundException("Profile not found with ID: " + userId);
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        Comment comment = new Comment();
        comment.setProfile(profile);
        comment.setPost(post);
        comment.setContent(commentDto.getContent());

        Comment savedComment = commentRepository.save(comment);
        logger.info("Comment created successfully with ID: {} on post ID: {}", savedComment.getId(), postId);

        return convertToCommentResponseDto(savedComment);
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(Long userId, Long commentId, CommentDto commentDto) {
        logger.info("Attempting to update comment with ID: {} by user ID: {}", commentId, userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + commentId));

        if (!comment.getProfile().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("User does not own this comment");
        }

        comment.setContent(commentDto.getContent());
        Comment updatedComment = commentRepository.save(comment);
        logger.info("Comment updated successfully with ID: {}", commentId);

        return convertToCommentResponseDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        logger.info("Attempting to delete comment with ID: {} by user ID: {}", commentId, userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + commentId));

        if (!comment.getProfile().getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("User does not own this comment");
        }

        commentRepository.deleteById(commentId);
        logger.info("Comment deleted successfully with ID: {}", commentId);
    }

    @Override
    public List<CommentResponseDto> getPostComments(Long postId) {
        logger.info("Retrieving comments for post ID: {}", postId);

        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
        logger.info("Found {} comments for post ID: {}", comments.size(), postId);

        return comments.stream()
                .map(this::convertToCommentResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto getComment(Long commentId) {
        logger.info("Retrieving comment with ID: {}", commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + commentId));

        return convertToCommentResponseDto(comment);
    }

    private CommentResponseDto convertToCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .userId(comment.getProfile().getUser().getId())
                .username(comment.getProfile().getUser().getUsername())
                .postId(comment.getPost().getId())
                .build();
    }
}
