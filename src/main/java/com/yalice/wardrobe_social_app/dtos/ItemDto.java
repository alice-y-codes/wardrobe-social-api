package com.yalice.wardrobe_social_app.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDto {
    private Long id;
    private Long userId;
    private String name;
    private String brand;
    private String category;
    private String size;
    private String color;
    private String imageUrl;

    // Getters and Setters
}
