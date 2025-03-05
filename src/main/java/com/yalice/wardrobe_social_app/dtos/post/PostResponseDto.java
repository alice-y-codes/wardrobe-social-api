package com.yalice.wardrobe_social_app.dtos.post;

import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Post;
import com.yalice.wardrobe_social_app.enums.PostVisibility;
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
    private Outfit outfit;
    private String visibility;
    private String username;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.featureImage = post.getFeatureImage();
        this.content = post.getContent();
        this.outfit = post.getOutfit();
        this.visibility = post.getVisibility().name();
        this.username = post.getProfile().getUser().getUsername();
    }
}
