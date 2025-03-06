package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.dtos.user.UserDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.exceptions.UsernameAlreadyExistsException;
import org.springframework.stereotype.Service;

/**
 * Service interface for user management operations.
 * This interface defines the contract for user registration, profile updates,
 * and account management.
 */
@Service
public interface UserManagementService {
    /**
     * Registers a new user in the system.
     *
     * @param userDto The DTO containing user registration details.
     * @return The registered user as a response DTO.
     * @throws UsernameAlreadyExistsException if the username is already taken.
     */
    UserResponseDto registerUser(UserDto userDto);

    /**
     * Updates a user's profile information.
     *
     * @param userId  The ID of the user to update.
     * @param userDto The DTO containing the updated user information.
     * @return The updated user as a response DTO.
     */
    UserResponseDto updateUserProfile(Long userId, UserDto userDto);

    /**
     * Changes a user's password.
     *
     * @param userId      The ID of the user.
     * @param oldPassword The current password.
     * @param newPassword The new password to set.
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * Deletes a user account.
     *
     * @param userId The ID of the user to delete.
     */
    void deleteUser(Long userId);
}
