package com.yalice.wardrobe_social_app.dtos.friendship;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendResponseDto {
    private Long id;
    private Long userId;
    private String username;
    private String status;
    private LocalDateTime createdAt;
}