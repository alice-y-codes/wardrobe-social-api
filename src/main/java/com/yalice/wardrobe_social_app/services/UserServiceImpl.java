package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the UserService interface that provides user-related
 * operations.
 * This service handles user registration, authentication, and user data
 * management.
 */
@Service
public class UserServiceImpl implements UserService {

    /** Repository for user data persistence. */
    @Autowired
    private UserRepository userRepository;

    /** Encoder for password hashing. */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers a new user in the system.
     *
     * @param user The user to register
     * @return Optional containing the registered user if successful, empty if
     *         username exists
     */
    @Override
    public Optional<User> registerUser(final User user) {
        // Check if username exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return Optional.empty();
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return Optional.of(userRepository.save(user));
    }

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    @Override
    public Optional<User> findUserByUsername(final String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Finds a user by their ID.
     *
     * @param userId The ID of the user to find
     * @return Optional containing the user if found, empty otherwise
     */
    @Override
    public Optional<User> findById(final Long userId) {
        return userRepository.findById(userId);
    }
}