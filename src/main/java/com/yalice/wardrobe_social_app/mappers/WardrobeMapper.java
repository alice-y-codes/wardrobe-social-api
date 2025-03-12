package com.yalice.wardrobe_social_app.mappers;

import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import com.yalice.wardrobe_social_app.dtos.wardrobe.WardrobeResponseDto;
import com.yalice.wardrobe_social_app.entities.Wardrobe;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class WardrobeMapper {

    public WardrobeResponseDto toResponseDto(Wardrobe wardrobe) {

        Set<ItemResponseDto> itemResponseDtos = wardrobe.getItems().stream()
                .map(ItemResponseDto::new)
                .collect(Collectors.toSet());

        if (wardrobe == null) {
            return null;
        }

        return WardrobeResponseDto.builder()
                .id(wardrobe.getId())
                .name(wardrobe.getName())
                .profileId(wardrobe.getProfile().getId())
                .items(itemResponseDtos)
                .build();
    }
}
