package com.yalice.wardrobe_social_app.interfaces;

import com.yalice.wardrobe_social_app.entities.User;
import java.util.Optional;

/**
 * Service interface for user-related operations.
 * This interface defines the contract for user management operations
 * such as registration, authentication, and user data retrieval.
 */
public interface UserService {

    /**
     * Registers a new user in the system.
     *
     * @param user The user to register
     * @return Optional containing the registered user if successful, empty if
     *         username exists
     */
    Optional<User> registerUser(User user);

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findUserByUsername(String username);

    /**
     * Finds a user by their ID.
     *
     * @param userId The ID of the user to find
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findById(Long userId);
}
