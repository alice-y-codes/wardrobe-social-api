package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.dtos.user.UserDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.exceptions.UsernameAlreadyExistsException;

public interface UserManagementService
{
    /**
     * Registers a new user in the system.
     *
     * @param userDto The DTO containing user registration details.
     * @return The registered user as a response DTO.
     * @throws UsernameAlreadyExistsException if the username is already taken.
     */
    UserResponseDto registerUser(UserDto userDto);

}
