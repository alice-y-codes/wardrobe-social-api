package com.yalice.wardrobe_social_app.dtos.outfit;

import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class OutfitResponseDto {
    private Long id;
    private String name;
    private String description;
    private String season;
    private boolean isFavorite;
    private boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<ItemResponseDto> items; // Detailed items in the outfit
}
