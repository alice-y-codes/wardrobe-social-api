package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeedService {

    Page<Post> getUserFeed(Long userId, Pageable pageable);

    Page<Post> getUserPosts(Long userId, Long viewerId, Pageable pageable);
}