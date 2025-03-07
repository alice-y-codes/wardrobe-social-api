package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByProfileIdAndVisibilityInOrderByCreatedAtDesc(Long profileId, List<Post.PostVisibility> visibility,
                                                                  Pageable pageable);

    Page<Post> findByProfileIdAndVisibilityOrderByCreatedAtDesc(Long profileId, Post.PostVisibility visibility, Pageable pageable);

    Page<Post> findByProfileIdInOrderByCreatedAtDesc(List<Long> profileIds, Pageable pageable);

    Page<Post> findByProfileIdInAndOutfitSeasonOrderByCreatedAtDesc(List<Long> profileIds, String season, Pageable pageable);

    Page<Post> findByProfileIdInAndOutfitCategoryOrderByCreatedAtDesc(List<Long> profileIds, String category, Pageable pageable);
}
