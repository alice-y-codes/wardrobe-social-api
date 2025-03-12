package com.yalice.wardrobe_social_app.mappers;

import com.yalice.wardrobe_social_app.dtos.comment.CommentResponseDto;
import com.yalice.wardrobe_social_app.entities.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentResponseDto toResponseDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .userId(comment.getProfile().getUser().getId())
                .username(comment.getProfile().getUser().getUsername())
                .postId(comment.getPost().getId())
                .build();
    }
}
