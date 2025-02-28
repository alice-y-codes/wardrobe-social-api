package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.entities.Comment;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.PostVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface FeedService {
    Post createPost(Long userId, String content, Long outfitId, PostVisibility visibility);

    Optional<Post> getPost(Long postId, Long viewerId);

    Page<Post> getUserFeed(Long userId, Pageable pageable);

    Page<Post> getUserPosts(Long userId, Long viewerId, Pageable pageable);

    void deletePost(Long postId, Long userId);

    boolean likePost(Long postId, Long userId);

    boolean unlikePost(Long postId, Long userId);

    boolean hasUserLikedPost(Long postId, Long userId);

    long getPostLikeCount(Long postId);

    Comment addComment(Long postId, Long userId, String content);

    void deleteComment(Long commentId, Long userId);

    List<Comment> getPostComments(Long postId);

    Page<Comment> getPostComments(Long postId, Pageable pageable);
}