package com.yalice.wardrobe_social_app.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestDto {
    private Long requestId;
    private Long recipientId;
    private Long friendId;
}