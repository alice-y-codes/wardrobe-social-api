package com.yalice.wardrobe_social_app.dtos.profile;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDto {
    private Long id;
    private Long userId;
    private String username;
    private String bio;
    private String location;
    private String stylePreferences;
    private String favoriteBrands;
    private String fashionInspirations;
    private String profileImageUrl;
    private boolean isPublic;
}