package com.yalice.wardrobe_social_app.dtos;

import com.yalice.wardrobe_social_app.enums.PostVisibility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private String content;
    private Long outfitId;
    private PostVisibility visibility;
}