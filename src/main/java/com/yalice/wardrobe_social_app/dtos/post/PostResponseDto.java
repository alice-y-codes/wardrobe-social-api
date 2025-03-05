package com.yalice.wardrobe_social_app.dtos.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PostResponseDto {

    private Long id;
    private String title;
    private String featureImage;
    private String content;
    private Long outfitId;
    private String visibility;
    private String username;

    public PostResponseDto(Long id, String title, String featureImage, String content, Long outfitId, String visibility, String username) {
        this.id = id;
        this.title = title;
        this.featureImage = featureImage;
        this.content = content;
        this.outfitId = outfitId;
        this.visibility = visibility;
        this.username = username;
    }
}
