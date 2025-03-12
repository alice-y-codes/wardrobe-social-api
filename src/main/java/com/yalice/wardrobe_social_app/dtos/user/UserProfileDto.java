package com.yalice.wardrobe_social_app.dtos.user;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;
}