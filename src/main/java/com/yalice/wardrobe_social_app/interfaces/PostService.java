package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.dtos.PostDto;
import com.yalice.wardrobe_social_app.dtos.PostResponseDto;

public interface PostService {
    PostResponseDto createPost(Long userId, PostDto postDto);

    PostResponseDto getPost(Long postId, Long viewerId);

    void deletePost(Long postId, Long userId);

    PostResponseDto updatePost(Long postId, Long userId, PostDto postDto);

    boolean likePost(Long postId, Long userId);

    boolean unlikePost(Long postId, Long userId);
}
