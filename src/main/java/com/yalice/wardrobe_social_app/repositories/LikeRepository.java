package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Like;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByPostAndUser(Post post, User user);

    boolean existsByPostAndUser(Post post, User user);

    long countByPostId(Long postId);

    void deleteByPostAndUser(Post post, User user);
}