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

    Optional<User> findByUsername(String username);

    List<User> findByUsernameContainingIgnoreCase(String partialUsername);

    boolean existsByUsername(String username);
}
