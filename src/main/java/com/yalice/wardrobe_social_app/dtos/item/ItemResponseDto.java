package com.yalice.wardrobe_social_app.dtos.item;

import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.entities.Wardrobe;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ItemResponseDto {
    private Long id;
    private String name;
    private String brand;
    private String category;
    private String size;
    private String color;
    private String imageUrl;
    private Long userId;
    private Long wardrobeId;

    public ItemResponseDto(Item item, Wardrobe wardrobe, User user) {
        this.id = item.getId();
        this.name = item.getName();
        this.brand = item.getBrand();
        this.category = item.getCategory();
        this.size = item.getSize();
        this.color = item.getColor();
        this.imageUrl = item.getImageUrl();
        this.userId = user.getId();
        this.wardrobeId = wardrobe.getId();
    }
}
