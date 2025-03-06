package com.yalice.wardrobe_social_app.dtos.feed;

import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO representing a feed item that can be either an outfit or a post.
 */
@Data
public class FeedItemDto {
    private Long id;
    private String type; // "OUTFIT" or "POST"
    private OutfitResponseDto outfit;
    private UserResponseDto user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String season;
    private String category;
    private int likesCount;
    private int commentsCount;
    private boolean isLikedByCurrentUser;
}