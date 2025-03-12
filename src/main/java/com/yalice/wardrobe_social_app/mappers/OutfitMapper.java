package com.yalice.wardrobe_social_app.mappers;

import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.dtos.outfit.OutfitResponseDto;
import com.yalice.wardrobe_social_app.entities.Outfit;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OutfitMapper {
    public OutfitResponseDto toResponseDto(Outfit outfit) {
        if (outfit == null) {
            return null;
        }

        Set<ItemResponseDto> itemResponseDtos = outfit.getItems().stream()
                .map(ItemResponseDto::new)
                .collect(Collectors.toSet());

        return OutfitResponseDto.builder()
                .id(outfit.getId())
                .name(outfit.getName())
                .description(outfit.getDescription())
                .season(outfit.getSeason())
                .isFavorite(outfit.isFavorite())
                .isPublic(outfit.isPublic())
                .createdAt(outfit.getCreatedAt())
                .updatedAt(outfit.getUpdatedAt())
                .items(itemResponseDtos)
                .profileId(outfit.getProfile().getId())
                .build();
    }
}
