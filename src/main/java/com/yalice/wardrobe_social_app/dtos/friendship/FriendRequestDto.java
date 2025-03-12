package com.yalice.wardrobe_social_app.dtos.friendship;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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