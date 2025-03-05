package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.Comment;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.interfaces.CommentService;
import com.yalice.wardrobe_social_app.interfaces.ProfileService;
import com.yalice.wardrobe_social_app.repositories.CommentRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl extends BaseService implements CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ProfileService profileService;

    @Autowired
    public CommentServiceImpl(PostRepository postRepository,
                              CommentRepository commentRepository,
                              ProfileService profileService) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.profileService = profileService;
    }

    @Override
    public Comment addComment(Long postId, Long profileId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));

        Profile profile = profileService.getProfileEntityById(profileId);

        if (profile == null) {
            throw new IllegalArgumentException("Profile not found with ID: " + profileId);
        }

        Comment comment = Comment.builder()
                .post(post)
                .profile(profile)
                .content(content)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId, Long profileId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with ID: " + commentId));

        // Check if the profile is the comment owner or the post owner
        if (!comment.getProfile().getId().equals(profileId) && !comment.getPost().getProfile().getId().equals(profileId)) {
            throw new IllegalArgumentException("Only the comment owner or post owner can delete the comment");
        }

        commentRepository.delete(comment);
    }

    @Override
    public List<Comment> getPostComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    @Override
    public Page<Comment> getPostComments(Long postId, Pageable pageable) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId, pageable);
    }
}
