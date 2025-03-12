package com.yalice.wardrobe_social_app.dtos.profile;

import lombok.*;

/**
 * DTO for profile data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDto {
    private String bio;
    private boolean isPublic;
    private String location;
    private String stylePreferences;
    private String favoriteBrands;
    private String fashionInspirations;
}