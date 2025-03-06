package com.yalice.wardrobe_social_app.dtos.profile;

import lombok.Data;

/**
 * DTO for profile data.
 */
@Data
public class ProfileDto {
    private String bio;
    private boolean isPublic;
    private String location;
    private String stylePreferences;
    private String favoriteBrands;
    private String fashionInspirations;
}