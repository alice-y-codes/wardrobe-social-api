package com.yalice.wardrobe_social_app.dtos.outfit;

import lombok.*;

import java.util.Set;

/**
 * DTO representing the data for creating or updating an outfit, including associated item IDs and user ID.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutfitDto {

    private String name;
    private String description;
    private String season;
    private boolean isFavorite;
    private boolean isPublic;
    private Set<Long> itemIds;
    private Long userId;

}
