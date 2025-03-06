package com.yalice.wardrobe_social_app.dtos.friendship;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class FriendRequestDto {
    private Long id;
    private Long senderId;
    private String senderUsername;
    private Long recipientId;
    private String recipientUsername;
    private String status;
    private LocalDateTime createdAt;
}