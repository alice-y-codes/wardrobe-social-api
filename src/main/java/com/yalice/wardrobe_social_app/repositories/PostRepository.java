package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.entities.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserOrderByCreatedAtDesc(User user);

    Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user.id = ?1 ORDER BY p.createdAt DESC")
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT p FROM Post p WHERE p.user.id = ?1 ORDER BY p.createdAt DESC")
    Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.visibility = 'PUBLIC' ORDER BY p.createdAt DESC")
    Page<Post> findPublicPostsOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user.id IN ?1 OR p.visibility = 'PUBLIC' ORDER BY p.createdAt DESC")
    Page<Post> findFeedPostsForUser(List<Long> friendIds, Pageable pageable);
}