package com.yalice.wardrobe_social_app.dtos.outfit;

import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Outfit;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO representing the response data for an outfit, including associated items and profile.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutfitResponseDto {

    private Long id;
    private String name;
    private String description;
    private String season;
    private boolean isFavorite;
    private boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<ItemResponseDto> items;
    private Long profileId;

    /**
     * Constructor for creating OutfitResponseDto from an Outfit entity without the items set.
     *
     * @param outfit The Outfit entity.
     */
    public OutfitResponseDto(Outfit outfit) {
        this(outfit.getId(), outfit.getName(), outfit.getDescription(), outfit.getSeason(),
                outfit.isFavorite(), outfit.isPublic(), outfit.getCreatedAt(), outfit.getUpdatedAt(),
                convertItemsToDto((Set<Item>) outfit.getItems()), outfit.getProfile().getId());
    }

    /**
     * Helper method to convert a set of Item entities into a set of ItemResponseDto objects.
     *
     * @param items The set of Item entities.
     * @return Set<ItemResponseDto> The converted set of ItemResponseDto.
     */
    private static Set<ItemResponseDto> convertItemsToDto(Set<Item> items) {
        return items.stream()
                .map(item -> new ItemResponseDto(item.getId(), item.getName(), item.getBrand(),
                        item.getCategory(), item.getSize(), item.getColor(),
                        item.getImageUrl(), item.getProfile().getId(), item.getWardrobe().getId()))
                .collect(java.util.stream.Collectors.toSet());
    }
}
