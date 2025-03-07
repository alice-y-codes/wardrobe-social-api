package com.yalice.wardrobe_social_app.dtos.feed;

import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedItemResponseDto {
    private Long id;
    private String title;
    private String content;
    private String season;
    private String category;
    private int likesCount;
    private int commentsCount;
    private String featureImage;
    private String outfitImage;
    private Set<String> itemImages;
    private UserResponseDto user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
