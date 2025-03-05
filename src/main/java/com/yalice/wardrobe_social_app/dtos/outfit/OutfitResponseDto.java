package com.yalice.wardrobe_social_app.dtos.outfit;

import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.Outfit;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO representing the response data for an outfit, including associated items and user.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class OutfitResponseDto {

    private Long id; // Outfit ID
    private String name; // Name of the outfit
    private String description; // Description of the outfit
    private String season; // Season the outfit is designed for
    private boolean isFavorite; // Whether the outfit is marked as favorite
    private boolean isPublic; // Whether the outfit is public or private
    private LocalDateTime createdAt; // Timestamp when the outfit was created
    private LocalDateTime updatedAt; // Timestamp when the outfit was last updated
    private Set<ItemResponseDto> items; // Detailed items that make up the outfit
    private Long userId; // ID of the user who owns the outfit

    /**
     * Constructor for creating OutfitResponseDto from an Outfit entity without the items set.
     *
     * @param outfit The Outfit entity.
     */
    public OutfitResponseDto(Outfit outfit) {
        this(outfit.getId(), outfit.getName(), outfit.getDescription(), outfit.getSeason(),
                outfit.isFavorite(), outfit.isPublic(), outfit.getCreatedAt(), outfit.getUpdatedAt(),
                convertItemsToDto(outfit.getItems()), outfit.getUser().getId()); // Include userId
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
                        item.getImageUrl(), item.getUser().getId()))
                .collect(java.util.stream.Collectors.toSet());
    }
}
