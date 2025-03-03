package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    Comment addComment(Long postId, Long userId, String content);

    void deleteComment(Long commentId, Long userId);

    List<Comment> getPostComments(Long postId);

    Page<Comment> getPostComments(Long postId, Pageable pageable);
}
