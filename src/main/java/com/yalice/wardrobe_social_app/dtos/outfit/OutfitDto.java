package com.yalice.wardrobe_social_app.dtos.outfit;

import lombok.*;

import java.util.Set;

/**
 * DTO representing the data for creating or updating an outfit, including associated item IDs and user ID.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class OutfitDto {

    private String name; // Name of the outfit
    private String description; // Description of the outfit
    private String season; // Season the outfit is designed for
    private boolean isFavorite; // Whether the outfit is marked as favorite
    private boolean isPublic; // Whether the outfit is public or private
    private Set<Long> itemIds; // IDs of items included in the outfit
    private Long userId; // ID of the user who owns the outfit

}
