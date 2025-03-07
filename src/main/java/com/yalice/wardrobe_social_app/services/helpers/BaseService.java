package com.yalice.wardrobe_social_app.services.helpers;

import com.yalice.wardrobe_social_app.dtos.feed.FeedItemResponseDto;
import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeResponseDto;
import com.yalice.wardrobe_social_app.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Base service class that provides common functionality for all services.
 * This includes converting entities to response DTOs.
 */
public abstract class BaseService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


    protected UserResponseDto convertToUserResponseDto(User user) {
        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail());
    }

    protected ItemResponseDto convertToItemResponseDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .brand(item.getBrand())
                .category(item.getCategory())
                .size(item.getSize())
                .color(item.getColor())
                .imageUrl(item.getImageUrl())
                .wardrobeId(item.getWardrobe().getId())
                .profileId(item.getProfile().getId())
                .build();
    }

    protected WardrobeResponseDto convertToWardrobeResponseDto(Wardrobe wardrobe) {
        return WardrobeResponseDto.builder()
                .id(wardrobe.getId())
                .name(wardrobe.getName())
                .profileId(wardrobe.getProfile().getId())
                .build();
    }

    /**
     * Converts an Outfit entity to an OutfitResponseDto.
     *
     * @param outfit The Outfit entity to be converted.
     * @return OutfitResponseDto The response DTO for the outfit.
     */
    protected OutfitResponseDto convertToOutfitResponseDto(Outfit outfit) {
        Set<ItemResponseDto> itemResponseDtos = outfit.getItems().stream()
                .map(item -> new ItemResponseDto(item.getId(), item.getName(), item.getBrand(),
                        item.getCategory(), item.getSize(),
                        item.getColor(), item.getImageUrl(),
                        outfit.getProfile().getId(), item.getWardrobe().getId()))
                .collect(Collectors.toSet());

        return new OutfitResponseDto(
                outfit.getId(),
                outfit.getName(),
                outfit.getDescription(),
                outfit.getSeason(),
                outfit.isFavorite(),
                outfit.isPublic(),
                outfit.getCreatedAt(),
                outfit.getUpdatedAt(),
                itemResponseDtos,
                outfit.getProfile().getId()
        );
    }

    /**
     * Converts a Post entity to a PostResponseDto.
     *
     * @param post The Post entity to be converted.
     * @return PostResponseDto The response DTO for the post.
     */
    protected PostResponseDto convertToPostResponseDto(Post post) {
        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getFeatureImage(),
                post.getContent(),
                post.getOutfit(),
                post.getVisibility().name(),
                post.getProfile().getUser().getUsername()
        );
    }

    protected FeedItemResponseDto convertToFeedItemDto(Post post) {
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
                .user(convertToUserResponseDto(post.getProfile().getUser()))
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
