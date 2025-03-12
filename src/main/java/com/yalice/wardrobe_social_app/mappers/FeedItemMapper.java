package com.yalice.wardrobe_social_app.mappers;

import com.yalice.wardrobe_social_app.dtos.feed.FeedItemResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Post;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FeedItemMapper {

    private final UserMapper userMapper;

    public FeedItemMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public FeedItemResponseDto toResponseDto(Post post) {
        return FeedItemResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .season(post.getOutfit().getSeason())
                .category(post.getOutfit().getCategory())
                .likesCount(post.getLikes().size())
                .commentsCount(post.getComments().size())
                .featureImage(post.getFeatureImage())
                .outfitImage(post.getOutfit().getImageUrl())
                .itemImages(getItemImages(post))
                .user(userMapper.toResponseDto(post.getProfile().getUser()))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    private Set<String> getItemImages(Post post) {
        return post.getOutfit().getItems().stream()
                .map(Item::getImageUrl)
                .collect(Collectors.toSet());
    }
}
