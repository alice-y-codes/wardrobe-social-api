package com.yalice.wardrobe_social_app.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OutfitDto {
    private String title;
    private String description;
    private List<Long> itemIds; // List of item IDs
    private String imageUrl; // Optional thumbnail image

}
