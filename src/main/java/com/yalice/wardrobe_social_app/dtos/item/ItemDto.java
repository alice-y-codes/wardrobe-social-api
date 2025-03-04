package com.yalice.wardrobe_social_app.dtos.item;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private String name;
    private String brand;
    private String category; // (e.g., "Shoes", "Jacket", "Dress")
    private String size;
    private String color;
    private String imageUrl;
}
