package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.Comment;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.CommentService;
import com.yalice.wardrobe_social_app.interfaces.FriendshipService;
import com.yalice.wardrobe_social_app.interfaces.UserSearchService;
import com.yalice.wardrobe_social_app.repositories.CommentRepository;
import com.yalice.wardrobe_social_app.repositories.LikeRepository;
import com.yalice.wardrobe_social_app.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserSearchService userSearchService;
    private final FriendshipService friendshipService;

    @Autowired
    public CommentServiceImpl(PostRepository postRepository, CommentRepository commentRepository,
                           LikeRepository likeRepository, UserSearchService userSearchService,
                           FriendshipService friendshipService) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
        this.userSearchService = userSearchService;
        this.friendshipService = friendshipService;
    }

    @Override
    public Comment addComment(Long postId, Long userId, String content) {
        Optional<Post> postOptional = postRepository.findById(postId);
        Optional<User> userOptional = userSearchService.findById(userId);

        if (postOptional.isEmpty() || userOptional.isEmpty()) {
            throw new IllegalArgumentException("Post or user not found");
        }

        Post post = postOptional.get();
        User user = userOptional.get();

        // Create a new comment
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(content)
                .build();

        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isEmpty()) {
            throw new IllegalArgumentException("Comment not found with ID: " + commentId);
        }

        Comment comment = commentOptional.get();

        // Check if the user is the comment owner or the post owner
        if (!comment.getUser().getId().equals(userId) && !comment.getPost().getUser().getId().equals(userId)) {
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
