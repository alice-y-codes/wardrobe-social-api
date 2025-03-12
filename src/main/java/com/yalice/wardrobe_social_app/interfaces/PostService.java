package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.dtos.post.PostDto;
import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface PostService {
    PostResponseDto createPost(Long profileId, PostDto postDto, MultipartFile image);

    PostResponseDto getPost(Long postId, Long viewerId);

    void deletePost(Long postId, Long profileId);

    PostResponseDto updatePost(Long postId, Long profileId, PostDto postDto, MultipartFile image);

    boolean toggleLikePost(Long postId, Long profileId);

    Page<PostResponseDto> getFeedPosts(Long viewerId, Pageable pageable);
}
