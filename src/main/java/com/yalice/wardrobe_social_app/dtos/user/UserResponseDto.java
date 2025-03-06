package com.yalice.wardrobe_social_app.dtos.user;

import lombok.*;

/**
 * DTO used for returning user information. Includes user ID, username, email, and user-specific details.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    private Long id;
    private String username;
    private String email;
}
