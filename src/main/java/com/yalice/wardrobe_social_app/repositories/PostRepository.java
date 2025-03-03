package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.enums.PostVisibility;
import com.yalice.wardrobe_social_app.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // Retrieve posts by user, ordered by creation date
    List<Post> findByUserOrderByCreatedAtDesc(User user);

    // Retrieve posts by user with pagination, ordered by creation date
    Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // Retrieve posts by userId, ordered by creation date
    @Query("SELECT p FROM Post p WHERE p.user.id = ?1 ORDER BY p.createdAt DESC")
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Retrieve posts by userId with pagination, ordered by creation date
    @Query("SELECT p FROM Post p WHERE p.user.id = ?1 ORDER BY p.createdAt DESC")
    Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Retrieve public posts, ordered by creation date (for all users)
    @Query("SELECT p FROM Post p WHERE p.visibility = 'PUBLIC' ORDER BY p.createdAt DESC")
    Page<Post> findPublicPostsOrderByCreatedAtDesc(Pageable pageable);

    // Retrieve posts for a user and their friends, ordered by creation date (includes public posts)
    @Query("SELECT p FROM Post p WHERE p.user.id IN ?1 OR p.visibility = 'PUBLIC' ORDER BY p.createdAt DESC")
    Page<Post> findFeedPostsForUser(List<Long> friendIds, Pageable pageable);

    // Retrieve posts by userId and visibility, ordered by creation date
    @Query("SELECT p FROM Post p WHERE p.user.id = ?1 AND p.visibility IN ?2 ORDER BY p.createdAt DESC")
    Page<Post> findByUserIdAndVisibilityIn(Long userId, List<PostVisibility> visibilities, Pageable pageable);

    // Retrieve posts by userId and specific visibility, ordered by creation date
    @Query("SELECT p FROM Post p WHERE p.user.id = ?1 AND p.visibility = ?2 ORDER BY p.createdAt DESC")
    Page<Post> findByUserIdAndVisibility(Long userId, PostVisibility visibility, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user.id = ?1 AND p.visibility IN ?2 ORDER BY p.createdAt DESC")
    Page<Post> findByUserIdAndVisibilityInOrderByCreatedAtDesc(Long userId, List<PostVisibility> visibility, Pageable pageable);


    @Query("SELECT p FROM Post p WHERE p.user.id = ?1 AND p.visibility = ?2 ORDER BY p.createdAt DESC")
    Page<Post> findByUserIdAndVisibilityOrderByCreatedAtDesc(Long userId, PostVisibility visibility, Pageable pageable);

}