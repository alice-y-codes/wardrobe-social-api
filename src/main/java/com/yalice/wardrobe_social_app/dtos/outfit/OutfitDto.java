package com.yalice.wardrobe_social_app.dtos.outfit;

import lombok.Data;
import java.util.Set;

@Data
public class OutfitDto {
    private String name;
    private String description;
    private String season;
    private boolean isFavorite;
    private boolean isPublic;
    private Set<Long> itemIds; // IDs of items in the outfit
}
