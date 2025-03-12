package com.yalice.wardrobe_social_app.services.social;

import com.yalice.wardrobe_social_app.dtos.comment.CommentDto;
import com.yalice.wardrobe_social_app.dtos.comment.CommentResponseDto;
import com.yalice.wardrobe_social_app.entities.Comment;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.interfaces.CommentService;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.mappers.CommentMapper;
import com.yalice.wardrobe_social_app.repositories.CommentRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl extends BaseService<Comment, Long> implements CommentService {

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
    protected JpaRepository<Comment, Long> getRepository() {
        return commentRepository;
    }

    @Override
    protected String getEntityName() {
        return "Comment";
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(Long profileId, Long postId, CommentDto commentDto) {
        logger.info("Creating comment on post ID: {} by profile ID: {}", postId, profileId);

        validationService.validateNotNull(profileId, "Profile ID");
        validationService.validateNotNull(postId, "Post ID");
        validationService.validateNotNull(commentDto, "Comment data");
        validationService.validateStringNotEmpty(commentDto.getContent(), "Comment content");

        Profile profile = profileService.getProfileEntityById(profileId);
        validationService.validateNotNull(profile, "Profile");

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));
        validationService.validateNotNull(post, "Post");

        // Validate post visibility for the commenter
        validatePostAccessibility(post, profile);

        Comment comment = buildComment(commentDto, profile, post);
        return mapEntity(save(comment), commentMapper::toResponseDto);
    }

    @Override
    @Transactional
    public CommentResponseDto updateComment(Long profileId, Long commentId, CommentDto commentDto) {
        logger.info("Updating comment with ID: {} by profile ID: {}", commentId, profileId);

        validationService.validateNotNull(profileId, "Profile ID");
        validationService.validateNotNull(commentId, "Comment ID");
        validationService.validateNotNull(commentDto, "Comment data");
        validationService.validateStringNotEmpty(commentDto.getContent(), "Comment content");

        Comment comment = findById(commentId);
        validationService.validateOwnership(comment.getProfile(), profileId, "comment");

        // Validate post still exists and is accessible
        validatePostAccessibility(comment.getPost(), comment.getProfile());

        comment.setContent(commentDto.getContent());
        return mapEntity(save(comment), commentMapper::toResponseDto);
    }

    @Override
    @Transactional
    public void deleteComment(Long profileId, Long commentId) {
        logger.info("Deleting comment with ID: {} by profile ID: {}", commentId, profileId);

        validationService.validateNotNull(profileId, "Profile ID");
        validationService.validateNotNull(commentId, "Comment ID");

        Comment comment = findById(commentId);
        validationService.validateOwnership(comment.getProfile(), profileId, "comment");

        delete(commentId);
        logger.info("Comment deleted successfully: {}", commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getPostComments(Long postId) {
        logger.info("Fetching comments for post ID: {}", postId);

        validationService.validateNotNull(postId, "Post ID");

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));
        validationService.validateNotNull(post, "Post");

        return mapEntityList(
                commentRepository.findByPostIdOrderByCreatedAtDesc(postId),
                commentMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponseDto getComment(Long commentId) {
        logger.info("Fetching comment with ID: {}", commentId);

        validationService.validateNotNull(commentId, "Comment ID");
        return mapEntity(findById(commentId), commentMapper::toResponseDto);
    }

    private Comment buildComment(CommentDto commentDto, Profile profile, Post post) {
        return Comment.builder()
                .profile(profile)
                .post(post)
                .content(commentDto.getContent())
                .build();
    }

    private void validatePostAccessibility(Post post, Profile profile) {
        validationService.validateExists(post != null &&
                (post.getVisibility() == Post.PostVisibility.PUBLIC ||
                        post.getProfile().getId().equals(profile.getId())),
                "Post is not accessible");
    }
}
