package com.yalice.wardrobe_social_app.dtos.post;

import com.yalice.wardrobe_social_app.entities.Outfit;
import com.yalice.wardrobe_social_app.entities.Post;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
