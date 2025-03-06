package com.yalice.wardrobe_social_app.dtos.post;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {

    private String title;
    private String featureImage;
    private String content;
    private Long outfitId;
    private String visibility;

}
