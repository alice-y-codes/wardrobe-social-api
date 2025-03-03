package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.enums.PostVisibility;

public interface PostService {
    Post createPost(Long userId, String content, Long outfitId, PostVisibility visibility);

    Post getPost(Long postId, Long viewerId);

    void deletePost(Long postId, Long userId);

    Post updatePost(Long postId, Post updatedPost);

    boolean likePost(Long postId, Long userId);

    boolean unlikePost(Long postId, Long userId);
}
