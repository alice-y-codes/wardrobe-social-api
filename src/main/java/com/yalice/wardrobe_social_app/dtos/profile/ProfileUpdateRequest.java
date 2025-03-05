package com.yalice.wardrobe_social_app.dtos.profile;

import com.yalice.wardrobe_social_app.entities.Profile.ProfileVisibility;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {
    private String bio;
    private ProfileVisibility visibility;
}