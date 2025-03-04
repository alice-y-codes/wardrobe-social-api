package com.yalice.wardrobe_social_app.dtos.item;

import com.yalice.wardrobe_social_app.entities.Item;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponseDto {
    private Long id;
    private String name;
    private String brand;
    private String category; // (e.g., "Shoes", "Jacket", "Dress")
    private String size;
    private String color;
    private String imageUrl;
    private Long userId;

    public ItemResponseDto(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.brand = item.getBrand();
        this.category = item.getCategory();
        this.size = item.getSize();
        this.color = item.getColor();
        this.imageUrl = item.getImageUrl();
        this.userId = item.getUser().getId(); // Assuming the Item has a 'user' field
    }


}
