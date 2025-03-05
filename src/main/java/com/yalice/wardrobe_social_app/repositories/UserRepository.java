package com.yalice.wardrobe_social_app.repositories;

import com.yalice.wardrobe_social_app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Provides methods for user data persistence and retrieval.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds users whose username contains the given partial username (case-insensitive).
     *
     * @param partialUsername The partial username to search for
     * @return List of users matching the partial username
     */
    List<User> findByUsernameContainingIgnoreCase(String partialUsername); // Enables partial search
}
