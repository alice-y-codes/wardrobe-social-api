package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.Comment;
import com.yalice.wardrobe_social_app.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);

    Page<Comment> findByPostOrderByCreatedAtAsc(Post post, Pageable pageable);

    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    Page<Comment> findByPostIdOrderByCreatedAtAsc(Long postId, Pageable pageable);

    long countByPostId(Long postId);

    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);
}