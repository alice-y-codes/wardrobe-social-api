package com.yalice.wardrobe_social_app.dtos.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private String username;
    private String password;
    private String email;
}
