package com.yalice.wardrobe_social_app.mappers;

import com.yalice.wardrobe_social_app.dtos.friendship.FriendRequestDto;
import com.yalice.wardrobe_social_app.dtos.friendship.FriendResponseDto;
import com.yalice.wardrobe_social_app.entities.Friendship;
import org.springframework.stereotype.Component;

@Component
public class FriendMapper {

    public FriendResponseDto toResponseDto(Friendship friendship) {
        if (friendship.getSender() == null || friendship.getRecipient() == null) {
            throw new IllegalStateException("Friendship is missing sender or recipient.");
        }

        return FriendResponseDto.builder()
                .id(friendship.getId())
                .userId(friendship.getSender().getId().equals(friendship.getRecipient().getId())
                        ? friendship.getRecipient().getId()
                        : friendship.getSender().getId())
                .username(friendship.getSender().getId().equals(friendship.getRecipient().getId())
                        ? friendship.getRecipient().getUsername()
                        : friendship.getSender().getUsername())
                .status(friendship.getStatus().name())
                .build();
    }

    public FriendRequestDto toRequestDto(Friendship friendship) {
        if (friendship.getSender() == null || friendship.getRecipient() == null) {
            throw new IllegalStateException("Friendship is missing sender or recipient.");
        }

        return FriendRequestDto.builder()
                .id(friendship.getId())
                .senderId(friendship.getSender().getId())
                .recipientId(friendship.getRecipient().getId())
                .status(friendship.getStatus().name())
                .build();
    }
}
