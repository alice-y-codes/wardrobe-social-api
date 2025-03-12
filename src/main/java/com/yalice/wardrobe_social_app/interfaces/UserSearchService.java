package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.UserNotFoundException;

import java.util.List;

/**
 * Service interface for user search operations.
 * This interface defines the contract for retrieving user data.
 */
public interface UserSearchService {

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for.
     * @return The user response DTO.
     * @throws UserNotFoundException if the user is not found.
     */
    UserResponseDto getUserByUsername(String username);

    /**
     * Finds a user by their ID.
     *
     * @param userId The ID of the user to find.
     * @return The user response DTO.
     * @throws UserNotFoundException if the user is not found.
     */
    UserResponseDto getUserById(Long userId);

    /**
     * Finds a user entity by their ID.
     *
     * @param userId The ID of the user to find.
     * @return The user entity
     * @throws UserNotFoundException if the user is not found.
     */
    User getUserEntityById(Long userId);

    /**
     * Searches for users whose username contains the given partial string.
     *
     * @param partialUsername The partial username to search for.
     * @return A list of matching users in response DTO format.
     */
    List<UserResponseDto> searchUsersByUsername(String partialUsername);

    /**
     * Retrieves all users with pagination.
     *
     * @param page The page number (zero-based index).
     * @param size The number of users per page.
     * @return A paginated list of user response DTOs.
     */
    List<UserResponseDto> getAllUsers(int page, int size);
}
