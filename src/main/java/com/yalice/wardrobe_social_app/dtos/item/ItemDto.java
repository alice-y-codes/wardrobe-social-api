package com.yalice.wardrobe_social_app.dtos.item;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ItemDto {
    private String name;
    private String brand;
    private String category;
    private String size;
    private String color;
    private String imageUrl;
}
