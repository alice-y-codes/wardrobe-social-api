package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.enums.PostVisibility;

import java.util.Optional;

public interface PostService {
    Post createPost(Long userId, String content, Long outfitId, PostVisibility visibility);

    Optional<Post> getPost(Long postId, Long viewerId);

    void deletePost(Long postId, Long userId);

    boolean likePost(Long postId, Long userId);

    boolean unlikePost(Long postId, Long userId);

    boolean hasUserLikedPost(Long postId, Long userId);

    long getPostLikeCount(Long postId);
}
