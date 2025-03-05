package com.yalice.wardrobe_social_app.dtos.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PostDto {

    private String title;
    private String featureImage;
    private String content;
    private Long outfitId;
    private String visibility;

}
