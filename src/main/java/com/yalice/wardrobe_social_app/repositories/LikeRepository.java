package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Like;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.Profile;
import com.yalice.wardrobe_social_app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByPostAndProfile(Post post, Profile profile);
    boolean existsByPostAndProfile(Post post, Profile profile);
}