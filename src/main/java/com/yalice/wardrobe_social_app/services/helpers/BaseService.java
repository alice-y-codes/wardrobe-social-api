package com.yalice.wardrobe_social_app.services.helpers;

import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeResponseDto;
import com.yalice.wardrobe_social_app.entities.*;
import com.yalice.wardrobe_social_app.interfaces.DtoConverterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Base service class that provides common functionality for all services.
 * This includes converting entities to response DTOs.
 */
public abstract class BaseService implements DtoConverterService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public UserResponseDto convertToUserResponseDto(User user) {
        return new UserResponseDto(user.getId(), user.getUsername(), user.getEmail());
    }
    @Override
    public ItemResponseDto convertToItemResponseDto(Item item, User user) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .brand(item.getBrand())
                .category(item.getCategory())
                .size(item.getSize())
                .color(item.getColor())
                .imageUrl(item.getImageUrl())
                .userId(user.getId())
                .build();
    }


    @Override
    public ItemResponseDto convertToItemResponseDto(Item item , Wardrobe wardrobe, User user) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .brand(item.getBrand())
                .category(item.getCategory())
                .size(item.getSize())
                .color(item.getColor())
                .imageUrl(item.getImageUrl())
                .wardrobeId(wardrobe.getId())
                .userId(user.getId())
                .build();
    }

    @Override
    public WardrobeResponseDto convertToWardrobeResponseDto(Wardrobe wardrobe) {
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
    public OutfitResponseDto convertToOutfitResponseDto(Outfit outfit) {
        Set<ItemResponseDto> itemResponseDtos = outfit.getItems().stream()
                .map(item -> new ItemResponseDto(item.getId(), item.getName(), item.getBrand(),
                        item.getCategory(), item.getSize(),
                        item.getColor(), item.getImageUrl(),
                        outfit.getUser().getId()))
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
                outfit.getUser().getId()
        );
    }

    /**
     * Converts a Post entity to a PostResponseDto.
     *
     * @param post The Post entity to be converted.
     * @param user The User entity associated with the Post, to retrieve username.
     * @return PostResponseDto The response DTO for the post.
     */
    public PostResponseDto convertToPostResponseDto(Post post, User user) {
        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getFeatureImage(),
                post.getContent(),
                post.getOutfit().getId(),
                post.getVisibility().name(),
                user.getUsername()
        );
    }
}
