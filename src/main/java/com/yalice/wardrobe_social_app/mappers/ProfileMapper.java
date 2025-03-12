package com.yalice.wardrobe_social_app.mappers;

import com.yalice.wardrobe_social_app.dtos.profile.ProfileResponseDto;
import com.yalice.wardrobe_social_app.entities.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileMapper {

    public ProfileResponseDto toResponseDto(Profile profile) {
        if (profile == null) {
            return null;
        }

        return ProfileResponseDto.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .username(profile.getUser().getUsername())
                .bio(profile.getBio())
                .location(profile.getLocation())
                .stylePreferences(profile.getStylePreferences())
                .favoriteBrands(profile.getFavoriteBrands())
                .fashionInspirations(profile.getFashionInspirations())
                .profileImageUrl(profile.getProfileImageUrl())
                .isPublic(profile.getVisibility() == Profile.ProfileVisibility.PUBLIC)
                .build();
    }

}
