package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.comment.CommentDto;
import com.yalice.wardrobe_social_app.dtos.comment.CommentResponseDto;
import com.yalice.wardrobe_social_app.entities.Comment;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
import com.yalice.wardrobe_social_app.interfaces.CommentService;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.mappers.CommentMapper;
import com.yalice.wardrobe_social_app.repositories.CommentRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends BaseService implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ProfileService profileService;
    private final CommentMapper commentMapper;

    public CommentServiceImpl(
            CommentRepository commentRepository,
            PostRepository postRepository,
            ProfileService profileService,
            CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.profileService = profileService;
        this.commentMapper = commentMapper;
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(Long profileId, Long postId, CommentDto commentDto) {
        logger.info("Creating comment on post ID: {} by profile ID: {}", postId, profileId);

        Profile profile = getValidProfileById(profileId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        Comment comment = new Comment();
        comment.setProfile(profile);
        comment.setPost(post);
        comment.setContent(commentDto.getContent());

        Comment savedComment = commentRepository.save(comment);
        logger.info("Comment created with ID: {} on post ID: {}", savedComment.getId(), postId);

        return commentMapper.toResponseDto(savedComment);
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(Long profileId, Long commentId, CommentDto commentDto) {
        logger.info("Updating comment with ID: {} by profile ID: {}", commentId, profileId);

        Comment comment = findCommentById(commentId);
        validateCommentOwnership(profileId, comment);

        comment.setContent(commentDto.getContent());
        Comment updatedComment = commentRepository.saveAndFlush(comment);
        logger.info("Comment updated successfully with ID: {}", commentId);

        return commentMapper.toResponseDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long profileId, Long commentId) {
        logger.info("Deleting comment with ID: {} by profile ID: {}", commentId, profileId);

        Comment comment = findCommentById(commentId);
        validateCommentOwnership(profileId, comment);

        commentRepository.deleteById(commentId);
        logger.info("Comment deleted with ID: {}", commentId);
    }

    @Override
    public List<CommentResponseDto> getPostComments(Long postId) {
        logger.info("Fetching comments for post ID: {}", postId);

        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
        logger.info("Retrieved {} comments for post ID: {}", comments.size(), postId);

        return comments.stream()
                .map(commentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto getComment(Long commentId) {
        logger.info("Fetching comment with ID: {}", commentId);
        Comment comment = findCommentById(commentId);
        return commentMapper.toResponseDto(comment);
    }

    private Profile getValidProfileById(Long profileId) {
        return profileService.getProfileEntityById(profileId);
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + commentId));
    }

    private void validateCommentOwnership(Long profileId, Comment comment) {
        if (!comment.getProfile().getId().equals(profileId)) {
            throw new ResourceNotFoundException("Profile does not own this comment");
        }
    }
}
