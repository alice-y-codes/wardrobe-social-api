package com.yalice.wardrobe_social_app.dtos.user;

import lombok.*;

/**
 * DTO used for returning user information. Includes user ID, username, email, and user-specific details.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserResponseDto {

    private Long id; // User ID
    private String username; // User's username
    private String email; // User's email
}
