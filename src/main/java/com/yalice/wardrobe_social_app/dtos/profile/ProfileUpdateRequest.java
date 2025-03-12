package com.yalice.wardrobe_social_app.dtos.profile;

import com.yalice.wardrobe_social_app.entities.Profile.ProfileVisibility;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileUpdateRequest {
    private String bio;
    private ProfileVisibility visibility;
}