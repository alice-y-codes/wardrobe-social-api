package com.yalice.wardrobe_social_app.mappers;

import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.entities.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public ItemResponseDto toResponseDto(Item item) {
        if (item == null) {
            return null;
        }

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
}
