package com.yalice.wardrobe_social_app.dtos.friendship;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class FriendshipResponseDto {
    private Long id;
    private Long userId;
    private String username;
    private String status;
    private LocalDateTime createdAt;
}