package com.yalice.wardrobe_social_app.dtos.wardrobe;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WardrobeResponseDto {
    private Long id;
    private String name;
    private Long profileId;
}
