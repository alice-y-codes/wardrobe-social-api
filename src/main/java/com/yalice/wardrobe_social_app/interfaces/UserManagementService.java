package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.dtos.user.ChangePasswordDto;
import com.yalice.wardrobe_social_app.dtos.user.UserRegistrationDto;
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
     * @param registrationDto The DTO containing user registration details.
     * @return The registered user as a response DTO.
     * @throws UsernameAlreadyExistsException if the username is already taken.
     */
    UserResponseDto registerUser(UserRegistrationDto registrationDto);


    /**
     * Changes a user's password.
     *
     * @param userId      The ID of the user.
     * @param passwordDto The DTO containing the new password.
     */
    void changePassword(Long userId, ChangePasswordDto passwordDto);

    /**
     * Deletes a user account.
     *
     * @param userId The ID of the user to delete.
     */
    void deleteUser(Long userId);

    /**
     * Checks if a user exists by username.
     *
     * @param username The username to check.
     * @return True if the user exists, false otherwise.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists by ID.
     *
     * @param userId The user ID to check.
     * @return True if the user exists, false otherwise.
     */
    boolean existsById(Long userId);
}
