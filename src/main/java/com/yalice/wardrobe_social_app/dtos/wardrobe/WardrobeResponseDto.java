package com.yalice.wardrobe_social_app.dtos.wardrobe;

import com.yalice.wardrobe_social_app.dtos.item.ItemResponseDto;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WardrobeResponseDto {
    private Long id;
    private String name;
    private Long profileId;
    Set<ItemResponseDto> items;
}
