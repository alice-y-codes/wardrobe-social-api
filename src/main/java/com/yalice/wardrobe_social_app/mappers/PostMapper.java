package com.yalice.wardrobe_social_app.mappers;

import com.yalice.wardrobe_social_app.dtos.post.PostResponseDto;
import com.yalice.wardrobe_social_app.entities.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public PostResponseDto toResponseDto(Post post) {
        if (post == null) {
            return null;
        }

        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .featureImage(post.getFeatureImage())
                .content(post.getContent())
                .outfit(post.getOutfit())
                .visibility(post.getVisibility().name())
                .username(post.getProfile().getUser().getUsername())
                .build();
    }

}
