package com.yalice.wardrobe_social_app.services;

import com.yalice.wardrobe_social_app.dtos.user.UserDto;
import com.yalice.wardrobe_social_app.dtos.user.UserResponseDto;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.exceptions.UserNotFoundException;
import com.yalice.wardrobe_social_app.exceptions.UsernameAlreadyExistsException;
import com.yalice.wardrobe_social_app.repositories.UserRepository;
import com.yalice.wardrobe_social_app.services.helpers.BaseService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the UserManagementService that handles user registration, profile updates, password changes, and deletions.
 * This service extends BaseService to reuse common functionality like DTO conversion.
 */
@Service
public class UserManagementServiceImpl extends BaseService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor to inject the UserRepository and PasswordEncoder dependencies.
     *
     * @param userRepository The repository for interacting with user data.
     * @param passwordEncoder The encoder used for hashing passwords.
     */
    public UserManagementServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user. Checks if the username already exists, and if not, saves the new user to the database.
     *
     * @param userDto The data transfer object containing user details.
     * @return A UserResponseDto containing the registered user's information.
     * @throws UsernameAlreadyExistsException If the username is already taken.
     */
    @Transactional
    public UserResponseDto registerUser(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists: " + userDto.getUsername());
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        User savedUser = userRepository.save(user);
        logger.info("User '{}' registered successfully.", savedUser.getUsername());

        return convertToUserResponseDto(savedUser);
    }

    /**
     * Updates the user's profile (username and email).
     *
     * @param userId The ID of the user to update.
     * @param userDto The data transfer object containing the new user details.
     * @return A UserResponseDto containing the updated user information.
     * @throws UserNotFoundException If no user is found with the given user ID.
     */
    @Transactional
    public UserResponseDto updateUserProfile(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        logger.info("Found User: ID={} Username={} Email={}", user.getId(), user.getUsername(), user.getEmail());

        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());

        User updatedUser = userRepository.save(user);
        logger.info("User '{}' updated successfully.", updatedUser.getUsername());
        logger.info("User '{}' updated successfully.", updatedUser.getEmail());


        return convertToUserResponseDto(updatedUser);
    }

    /**
     * Changes the user's password after validating the old password.
     *
     * @param userId The ID of the user whose password is being changed.
     * @param oldPassword The current password of the user.
     * @param newPassword The new password to set for the user.
     * @throws UserNotFoundException If no user is found with the given user ID.
     * @throws IllegalArgumentException If the old password is incorrect.
     */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect old password.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        logger.info("User '{}' changed password successfully.", user.getUsername());
    }

    /**
     * Deletes a user from the system.
     *
     * @param userId The ID of the user to delete.
     * @throws UserNotFoundException If no user is found with the given user ID.
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        userRepository.delete(user);
        logger.info("User '{}' deleted successfully.", user.getUsername());
    }
}
